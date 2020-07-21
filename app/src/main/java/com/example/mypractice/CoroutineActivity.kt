package com.example.mypractice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.mypractice.utils.*
import kotlinx.coroutines.*

class CoroutineActivity : AppCompatActivity() {
    val tag = this.javaClass.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coroutine)
    }

    override fun onDestroy() {
        super.onDestroy()
        testScope.cancel()
    }
    fun test(view: View) {
//        test1()
//        test2()
//        test3()

//        test4()
//        test5()
//        test6()
        test7()
    }
    val testScope = CoroutineScope(SupervisorJob() +Dispatchers.IO)
    fun test7(){
        testScope.launch(CoroutineExceptionHandler{error->
            Log.e(tag,"error:$error")
        }) {
            supervisorScope {
                launch {
                    delay(2000)
                    log("testScope launch")
                }
                launch {
                    delay(2000)
                    log("testScope launch2")
                }
                launch {
                    delay(1000)
                    throw Exception("testScope error")
                }
            }

        }
    }
    val handler = CoroutineExceptionHandler {
            context, exception -> Log.e(tag,"Caught $exception")
    }
    fun test6(){
        doLaunchTryError({
            Log.e(tag, it)
        }){
//            val result = runCatching {
                log("testcoroutineScope start")
                val result = testcoroutineScope()
                log("testcoroutineScope end result:$result")
//                result
//            }
//            Log.d(tag,"result.isSuccess:${result.isSuccess}")
        }
    }
    suspend fun testcoroutineScope():Int{
        return supervisorScope{
//            repeat(5){time->
//                launch {
//                    delay(2000)
//                    log("launch time:$time")
//                }
//                async {
//                    delay(2000)
//                    log("async time:$time")
//                }
//            }
            launch{
                delay(100)
                log("launch")
                throw Exception("test")
            }
            val result1= async {
//                runCatching {
                    delay(1000)
                    log("result1")
                    val result = 1/1
                    result
//                }.getOrDefault(-1)

            }
            val result2 = async {
                delay(2000)
                log("result2")
                2
            }
            val result3 = async {
                delay(3000)
                log("result3")
                3
            }
            result1.await() + result2.await()+ result3.await()
        }
    }
    fun test5(){
                for (j in 1..10){
                    GlobalScopeLauchIO {
                        log("j:$j")
                        for (i in 1..10){
                            GlobalScopeLauchIO {
                                log("GlobalScopeLauchIO j:$j,i:$i")
                            }
                        }
                    }
            }
    }
    fun log(log:String){
        Log.d(tag,"----------Thread.currentThread ${Thread.currentThread().name}--> $log")
    }

    fun test4(){
        doLaunch {
            log("start")
            val sTime = System.currentTimeMillis()
            withIOContext {
                val result1= doAsync {
                    withIOContext {
                        delay(2000)
                        log("result1")
                    }
                }
                val result2= doAsync {
                    withIOContext {
                        delay(4000)
                        log("result2")
                    }

                }

                result1.await()
                result2.await()


            }
            log("end:${System.currentTimeMillis()-sTime}")
        }
    }
    fun test1(){
        doLaunch {
            log("0")
            for (j in 1..10){
                withIOContext {
                    log("j:$j")
                    for (i in 1..10){
                        withIOContext {
                            log("j:$j,i:$i")
                        }
                    }
                }
            }


        }
    }
    fun test2(){
        GlobalScopeLauchUI {
            log("GlobalScopeLauchUI")
            for (j in 1..10){
                withIOContext {
                    log("j:$j")
                    for (i in 1..10){
                        withIOContext {
                            log("j:$j,i:$i")
                        }
                    }
                }
            }
        }
    }
    fun test3(){
        GlobalScopeLauchIO {
            log("GlobalScopeLauchIO")
            for (j in 1..10){
                withIOContext {
                    log("j:$j")
                    for (i in 1..10){
                        withIOContext {
                            log("j:$j,i:$i")
                        }
                    }
                }
            }
        }
    }
}