package com.lanshifu.androidtransform.test;

import android.util.Log;

/**
 * @author lanxiaobin
 * @date 2020/11/29
 */
class Test {
    int add() {
        int i = 5;
        int j = 22;
        int k = i + j;
        return k;
    }

    void methodTime(){
        long startTime = System.currentTimeMillis();
        long cost = System.currentTimeMillis() - startTime;

        Log.v("","cost:" + cost);

    }
}
