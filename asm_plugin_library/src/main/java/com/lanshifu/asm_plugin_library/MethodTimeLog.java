package com.lanshifu.asm_plugin_library;

import android.util.Log;

/**
 * @author lanxiaobin
 * @date 2020/12/6
 */
class MethodTimeLog {

    public static String TAG = "MethodTime";

    public static void d(String text){
        Log.d(TAG, text);
    }

    public static void i(String text){
        Log.i(TAG, text);
    }

    public static void e(String text){
        Log.e(TAG, text);

    }
}
