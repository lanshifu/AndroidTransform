package com.lanshifu.plugin.base

interface TransformCallBack {
    fun process(className: String, classBytes: ByteArray?): ByteArray?
}