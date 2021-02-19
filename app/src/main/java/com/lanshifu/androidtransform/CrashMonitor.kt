package com.lanshifu.androidtransform

import android.app.Application
import android.os.Looper

/**
 * @author lanxiaobin
 * @date 2021/1/31
 */
object CrashMonitor {

    var application: Application? = null

    fun init(application: Application) {

        this.application = application
//        Looper.getMainLooper().
        Looper.myQueue().addIdleHandler {
            start()
            false
        }

    }

    fun start() {


        //监听所有Activity

        val defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            //先走bugly，再走这里
            defaultUncaughtExceptionHandler?.uncaughtException(thread, throwable)

            //所有状态清空，

            // 退出当前Activity（记录所有Activity）
        }
    }
}