package com.lanshifu.androidtransform

import android.util.Log

/**
 * @author lanxiaobin
 * @date 2020/11/28
 */
class Test {


    init {
        Log.d("TAG", "logd")
    }

    fun threadTest(){
        Thread{
            Log.d("TAG", "Thread run")

        }.apply {
            name = "thread name"
        }.start()
    }


    fun returnThreadTest():Thread{
        var thread = Thread()
        return thread
    }

    fun add():Int{
        val i = 11
        val j = 22
        return i+j
    }
}