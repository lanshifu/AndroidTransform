package com.lanshifu.androidtransform

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.HandlerThread
import android.util.Log
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        threadTest()
    }

    private fun threadTest() {

        thread {
            Log.d("TAG", "threadTest: thread")
            Thread.sleep(500000)
        }

        Thread{
            Log.d("TAG", "threadTest: Thread")
            Thread.sleep(500000)

        }.start()

    }

}