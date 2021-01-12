package com.example.mypractice

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.mypractice.utils.startActivity
import com.example.mypractice.view.GifMovieView
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class MainActivity : AppCompatActivity() {
    var TAG = this.javaClass.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        test()
    }

    fun media(view: View) {
        startActivity<MediaActivity>()
    }

    fun coroutin(view: View) {
//        startActivity<CoroutineActivity>()
        startActivity(Intent().setComponent(ComponentName(this,"com.WXPayEntryActivityAlias")))
    }
    @TargetApi(Build.VERSION_CODES.O)
    fun test(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel("", "", NotificationManager.IMPORTANCE_HIGH)
        }
    }


    fun testGif(view: View) {
        var moviePath = File(Environment.getExternalStorageDirectory(),"test.gif").absolutePath
        moviePath = "/storage/emulated/0/Android/data/com.dxcol5.cssx.gg1b/cache/72b8ebef248c6557e00f0a8cb78e18a1"
        Log.d(TAG,"size:${File(moviePath).length()}")
        GifMovieView(this).apply {
//            setMovieFilePath(moviePath)
//            gifResource = R.mipmap.test
            setMovieResource(R.mipmap.test)
            container.addView(this, ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.WRAP_CONTENT,ViewGroup.MarginLayoutParams.WRAP_CONTENT))
            container.requestLayout()

        }
    }

    fun webview(view: View) {
        startActivity<WebViewActivity>()
    }

    fun taskTest(view: View) {
        startActivity<TaskTestActivity>()
    }

    fun test(view: View) {}
}