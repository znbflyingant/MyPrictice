package com.example.mypractice

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.mypractice.utils.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class CoroutineActivity : AppCompatActivity() {
    val TAG = this.javaClass.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coroutine)
        Log.i(TAG, "onCreate: ${intent.component?.className}")
        val appInfo: ApplicationInfo = packageManager
            .getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        var testid = appInfo.metaData.getInt("testid").toString()
        Log.i(TAG, "testid:$testid")
    }

    override fun onDestroy() {
        super.onDestroy()
//        testScope.cancel()
    }

    fun test(view: View) {
//        test1()
//        test2()
//        test3()

//        test4()
//        test5()
//        test6()
        test7()
//        doLaunch {
//            testMutex()
//        }
    }

    val testScope = CoroutineScope(Job() + Dispatchers.IO)
    fun test7() {

        this.testScope.launch(CoroutineExceptionHandler { error ->
            Log.e(TAG, "error:$error")
        }) {
//            log(this.toString())
////            supervisorScope {
//                log(this.toString())
//                launch(SupervisorJob()) {
//                    log(this.toString())
//                    delay(2000)
//                    log("testScope launch")
//                }
//                launch() {
//                    delay(2000)
//                    log("testScope launch2")
//                }
//                launch() {
//                    delay(1000)
//                    throw Exception("testScope error")
//                }
//            }
            kotlin.runCatching {
                throw Exception("testScope error")

            }
        }

    }

    val handler = CoroutineExceptionHandler { context, exception ->
        Log.e(TAG, "Caught $exception")
    }

    fun test6() {
        doLaunchTryError({
            Log.e(TAG, it)
        }) {
//            val result = runCatching {
            log("testcoroutineScope start")
            val result = testcoroutineScope()
            log("testcoroutineScope end result:$result")
//                result
//            }
//            Log.d(TAG,"result.isSuccess:${result.isSuccess}")
        }
    }

    suspend fun testcoroutineScope(): Int {
        return supervisorScope {
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
            launch {
                delay(100)
                log("launch")
                throw Exception("test")
            }
            val result1 = async {
//                runCatching {
                delay(1000)
                log("result1")
                val result = 1 / 1
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
            result1.await() + result2.await() + result3.await()
        }
    }

    fun test5() {
        for (j in 1..10) {
            GlobalScopeLauchIO {
                log("j:$j")
                for (i in 1..10) {
                    GlobalScopeLauchIO {
                        log("GlobalScopeLauchIO j:$j,i:$i")
                    }
                }
            }
        }
    }

    fun log(log: String) {
        Log.d(TAG, "----------Thread.currentThread ${Thread.currentThread().name}--> $log")
    }

    fun test4() {
        doLaunch {
            log("start")
            val sTime = System.currentTimeMillis()
            withIOContext {
                val result1 = doAsync {
                    withIOContext {
                        delay(2000)
                        log("result1")
                    }
                }
                val result2 = doAsync {
                    withIOContext {
                        delay(4000)
                        log("result2")
                    }

                }

                result1.await()
                result2.await()


            }
            log("end:${System.currentTimeMillis() - sTime}")
        }
    }

    fun test1() {
        doLaunch {
            log("0")
            for (j in 1..10) {
                withIOContext {
                    log("j:$j")
                    for (i in 1..10) {
                        withIOContext {
                            log("j:$j,i:$i")
                        }
                    }
                }
            }


        }
    }

    fun test2() {
        GlobalScopeLauchUI {
            log("GlobalScopeLauchUI")
            for (j in 1..10) {
                withIOContext {
                    log("j:$j")
                    for (i in 1..10) {
                        withIOContext {
                            log("j:$j,i:$i")
                        }
                    }
                }
            }
        }
    }

    fun test3() {
        GlobalScopeLauchIO {
            log("GlobalScopeLauchIO")
            for (j in 1..10) {
                withIOContext {
                    log("j:$j")
                    for (i in 1..10) {
                        withIOContext {
                            log("j:$j,i:$i")
                        }
                    }
                }
            }
        }
    }


    val lock = Object()
    suspend fun testMutex() {
        val mutex = Mutex()
        for (i in 0..20) {
            doLaunch {
                synchronized(lock){

                }
                mutex.withLock {
                    val result = printWithIOContext(i)
                    Log.d(TAG, "result:$result")
                }
            }
        }
    }

    private suspend fun printWithIOContext(i: Int): Int {
        return withIOContext {
            Log.d(TAG, "$i start")
            delay(1000)
            Log.d(TAG, "$i end")
            i
        }
    }
}