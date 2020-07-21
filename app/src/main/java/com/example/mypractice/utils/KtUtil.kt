package com.example.mypractice.utils

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext




fun CoroutineExceptionHandler(callback :((result:String)->Unit)?):CoroutineExceptionHandler{
    return CoroutineExceptionHandler {
        CoroutineContext,Throwable->
            callback?.invoke("Caught $Throwable")
    }
}

inline fun <reified I : Activity> Activity.startActivity() {
    startActivity(Intent(this, I::class.java))
}
fun AppCompatActivity.doLaunchTryError(context: CoroutineContext, block: suspend CoroutineScope.() -> Unit): Job? {
    return this.lifecycleScope.launch(context, CoroutineStart.DEFAULT, block)
}
fun AppCompatActivity.doLaunchTryError(callback :((result:String)->Unit)?, block: suspend CoroutineScope.() -> Unit): Job? {
    return this.lifecycleScope.launch(CoroutineExceptionHandler{callback}, CoroutineStart.DEFAULT, block)
}
fun AppCompatActivity.doLaunch(block: suspend CoroutineScope.() -> Unit): Job? {
    return this.lifecycleScope.launch(EmptyCoroutineContext+Dispatchers.IO, CoroutineStart.DEFAULT, block)
}
fun AppCompatActivity.doLaunchIO(block: suspend CoroutineScope.() -> Unit): Job? {
    return this.lifecycleScope.launch(Dispatchers.IO, CoroutineStart.DEFAULT, block)
}
fun <T> AppCompatActivity.doAsync(block: suspend CoroutineScope.() -> T): Deferred<T> {
    return this.lifecycleScope.async(EmptyCoroutineContext, CoroutineStart.DEFAULT, block)
}
fun GlobalScopeLauchIO(block: suspend CoroutineScope.() -> Unit) : Job {
    return GlobalScope.launch(Dispatchers.IO,CoroutineStart.DEFAULT,block)
}
fun GlobalScopeLauchUI(block: suspend CoroutineScope.() -> Unit) : Job {
    return GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT,block)
}
suspend fun <T> withIOContext(block: suspend CoroutineScope.() -> T): T {
    return withContext(Dispatchers.IO, block)
}