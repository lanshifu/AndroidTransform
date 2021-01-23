package com.lanshifu.asm_plugin_library

import android.util.Log

/**
 * @author lanxiaobin
 * @date 2020/12/6
 */
internal object MethodTimeLog {
    var TAG = "MethodTime"
    fun d(text: String?) {
        Log.d(TAG, text!!)
    }

    fun i(text: String?) {
        Log.i(TAG, text!!)
    }

    fun e(text: String?) {
        Log.e(TAG, text!!)
    }
}