package com.example.mypractice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.mypractice.media.MediaOperation
import com.example.mypractice.utils.doLaunch
import com.example.mypractice.utils.withIOContext
import kotlinx.coroutines.withContext
import java.io.File

class MediaActivity : AppCompatActivity() {
    val TAG = "MediaActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)
    }
    fun test(view: View) {
        doLaunch {
            val sTime = System.currentTimeMillis()
            val result = withIOContext {
                val fileName =  "${System.currentTimeMillis()}.mp4"
                val resultPath = File(externalCacheDir,fileName)
//                MediaOperation().generate(this@MediaActivity,resultPath)
                MediaOperation().test(this@MediaActivity)


            }
            Log.d(TAG,"result:$result dTime:${System.currentTimeMillis()-sTime}")
        }
    }
}