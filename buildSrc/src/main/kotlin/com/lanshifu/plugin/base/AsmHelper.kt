package com.lanshifu.plugin.base

interface AsmHelper {
    fun modifyClass(srcClass: ByteArray?): ByteArray?
}