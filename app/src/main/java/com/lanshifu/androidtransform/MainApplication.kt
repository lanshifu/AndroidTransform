package com.lanshifu.androidtransform

import android.app.Application
import android.util.Log

/**
 * @author lanxiaobin
 * @date 2020/11/28
 */
class MainApplication:Application() {

//    init {
//
//        //
//        Log.d("TAG", ": init")
//
//        if (true){
//            add(1,2)
//        }
//
//        ShadowAsyncTask.optimizeAsyncTaskExecutor()
//    }
//
//    fun add(i:Int,j:Int):Int{
//        return i +j
//    }

    override fun onCreate() {
        super.onCreate()
        Log.d("TAG", ": init")
    }
}