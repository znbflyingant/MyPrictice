package com.example.mypractice

import kotlinx.coroutines.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    val handler = CoroutineExceptionHandler { context, exception ->
        println("context:$context,exception:$exception")
    }
    @Test
    fun test1() = runBlocking {
        println("start")
        val scope = CoroutineScope(SupervisorJob() + CoroutineName("test"))
        println("test")
        scope.launch(handler) {
            println("launch")
            launch(SupervisorJob()) {
                delay(1000)
                println("testScope launch1")
            }
            launch(SupervisorJob()) {
                delay(2000)
                println("testScope launch2")
            }
            launch(SupervisorJob()) {
                delay(500)
                throw Exception("testScope error")
            }
        }
        println("end")
        delay(Long.MAX_VALUE)
    }

    @Test
    fun test2() = runBlocking {
        println("start")
        val handler = CoroutineExceptionHandler { context, exception ->
            println("context:$context,exception:$exception")
        }
        val scope = CoroutineScope(Job() + CoroutineName("test"))
        println("test")
        scope.launch(handler) {
            //job
            supervisorScope {
                //supervisorJob
                println("launch")
                launch {
                    //supervisorJob
                    delay(1000)
                    println("testScope launch1")
                }
                launch {
                    //supervisorJob
                    delay(2000)
                    println("testScope launch2")
                }
                launch(Job()) {
                    delay(500)
                    throw Exception("testScope error")
                }
            }
        }
        println("end")
        delay(Long.MAX_VALUE)
    }

    @Test
    fun test3() = runBlocking {
        var context = Job() + Dispatchers.IO + CoroutineName("test")
        println("$context")
        println("${context[CoroutineName]}")
        println("${context[Job]}")
        context += CoroutineName("test2")
        println("$context")
        context = context.minusKey(Job)
        println("$context")
        val scope = CoroutineScope(context)
        scope.launch {
            println("${scope.coroutineContext}")
        }

        delay(Long.MAX_VALUE)
    }

    @Test
    fun test4() = runBlocking {
        var context = Job() + Dispatchers.IO + CoroutineName("test")
        println(context)
        context += Dispatchers.Default
        println(context)

    }

    @Test
    fun test5() {
        val result = kotlin.runCatching {
//           throw Exception("testScope error")
            1
        }.getOrDefault(0)
        println("result:$result")
    }

    @Test
    fun test6() {
        var result = 0
        try {
            throw Exception("testScope error")
//            result = 1
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        println("result:$result")
    }

    @Test
    fun test7() = runBlocking {
        coroutineScope {
            val asyncResult = async {
                throw Exception("testScope error")
                1
            }
            println("delay")
            delay(1000)
            val result = kotlin.runCatching {
                asyncResult.await()
            }.getOrDefault(0)
            println("result:$result")
        }
        delay(Long.MAX_VALUE)
    }

    @Test
    fun test8() = runBlocking {
        supervisorScope {
            val asyncResult = async {
                throw Exception("testScope error")
                1
            }
            println("delay")
            delay(1000)
            val result = kotlin.runCatching {
                asyncResult.await()
            }.getOrDefault(0)
            println("result:$result")
        }
        delay(Long.MAX_VALUE)
    }
    @Test
    fun test9() = runBlocking(handler) {
        coroutineScope {
            val asyncResult = async {
                throw Exception("testScope error")
                1
            }
            println("delay")
            delay(1000)
            val result = kotlin.runCatching {
                asyncResult.await()
            }.getOrDefault(0)
            println("result:$result")
        }
        delay(Long.MAX_VALUE)
    }
    @Test
    fun test10()= runBlocking{
        val scope = CoroutineScope(Job())
        scope.launch(handler) {
            launch {
                throwErrorTest()
            }
        }
        delay(Long.MAX_VALUE)
    }
    @Test
    fun test11()= runBlocking{
        val scope = CoroutineScope(Job())
        scope.launch {
            launch(handler) {
               throwErrorTest()
            }
        }
        delay(Long.MAX_VALUE)
    }
    fun throwErrorTest(){
        throw Exception("error test")
    }
}