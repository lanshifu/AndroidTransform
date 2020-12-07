package com.lanshifu.androidtransform;

import com.lanshifu.asm_plugin_library.MethodInfo;
import com.lanshifu.asm_plugin_library.MethodTimeUtil;

import java.util.List;

/**
 * @author lanxiaobin
 * @date 2020/12/1
 */
class TestJava {

    int add(){

        List<MethodInfo> methodInfos = MethodTimeUtil.obtainMethodCostData();
        MethodTimeUtil.start("");

        int i = 11;
        int j = 22;
        int k = i + j;
        return k;
    }
}
