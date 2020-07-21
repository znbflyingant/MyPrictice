package com.example.mypractice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.mypractice.utils.startActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun media(view: View) {
        startActivity<MediaActivity>()
    }

    fun coroutin(view: View) {
        startActivity<CoroutineActivity>()
    }
}