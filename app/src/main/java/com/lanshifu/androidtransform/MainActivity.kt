package com.lanshifu.androidtransform

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.HandlerThread
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import com.lanshifu.asm_plugin_library.ImageMonitorImageView
import com.lanshifu.asm_plugin_library.MethodTimeUtil
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        threadTest()

        findViewById<Button>(R.id.btnCollection).setOnClickListener {
            methodCost()
        }

        findViewById<ImageMonitorImageView>(R.id.imageView).setImageResource(R.mipmap.main_comment_bg)
        findViewById<ImageMonitorImageView>(R.id.imageView2).setImageResource(R.mipmap.main_comment_bg)


        CrashMonitor.init(application)
    }

    private fun methodCost() {
        val obtainMethodCostData = MethodTimeUtil.obtainMethodCostData()
        for (obtainMethodCostDatum in obtainMethodCostData) {
            Log.d("MainActivity", "onCreate: ${obtainMethodCostDatum}")
        }

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

        Thread.sleep(500)

    }

}