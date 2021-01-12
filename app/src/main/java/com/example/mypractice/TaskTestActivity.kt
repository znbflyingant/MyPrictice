package com.example.mypractice

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.mypractice.utils.task.Task
import com.google.gson.Gson

class TaskTestActivity : AppCompatActivity() {
    var TAG = this.javaClass.simpleName
    val task = Task.create()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_test)
    }
    class User{
        var name: String? = null
        var age = ""
    }
    fun test(view: View) {
        var user = User();
        var user2 = User();
        user2.name = ""
        val users = mutableListOf<User>()
        users.add(user)
        users.add(user2)
        val userInfos = users.iterator()
        while (userInfos.hasNext()) {
            val userInfo  = userInfos.next()
            if (userInfo.name == null) {
                userInfos.remove()
            }

        }
        Log.d(TAG,"gson:${Gson().toJson(users) }")

//        task.oneShot {
//            null
//        }
//        task.oneShot(1000){
//            null
//        }
//        task.repeat(2){
//            null
//        }
//        task.repeat(1000,2){
//            null
//        }
    }
}