package com.lanshifu.compress_image_plugin

open class CompressImagePluginConfig {
    var maxSize = 1024 * 1024.toFloat()
    var isCheckSize = true //是否检查大体积图片
    var optimizeType =
        OPTIMIZE_WEBP_CONVERT //优化方式，webp化、压缩图片
    var enableWhenDebug = true
    var isCheckPixels = true //是否检查大像素图片
    var maxWidth = 1080
    var maxHeight = 1080
    var whiteList = arrayOf<String>() //优化图片白名单
    var mctoolsDir = ""
    var isSupportAlphaWebp = false //是否支持webp化透明通道的图片,如果开启，请确保minSDK >= 18,或做了其他兼容措施
    var multiThread = true
    var bigImageWhiteList = arrayOf<String>() //大图检测白名单
    fun maxSize(maxSize: Float) {
        this.maxSize = maxSize
    }

    fun isCheckSize(check: Boolean) {
        isCheckSize = check
    }

    fun optimizeType(optimizeType: String) {
        this.optimizeType = optimizeType
    }

    fun isSupportAlphaWebp(isSupportAlphaWebp: Boolean) {
        this.isSupportAlphaWebp = isSupportAlphaWebp
    }

    fun enableWhenDebug(enableWhenDebug: Boolean) {
        this.enableWhenDebug = enableWhenDebug
    }

    fun isCheckPixels(checkSize: Boolean) {
        isCheckPixels = checkSize
    }

    fun maxWidth(maxWidth: Int) {
        this.maxWidth = maxWidth
    }

    fun maxHeight(maxHeight: Int) {
        this.maxHeight = maxHeight
    }

    fun whiteList(whiteList: Array<String>) {
        this.whiteList = whiteList
    }

    fun mctoolsDir(mctoolsDir: String) {
        this.mctoolsDir = mctoolsDir
    }

    fun maxStroageSize(maxSize: Float) {
        this.maxSize = maxSize
    }

    fun multiThread(multiThread: Boolean) {
        this.multiThread = multiThread
    }

    fun bigImageWhiteList(bigImageWhiteList: Array<String>) {
        this.bigImageWhiteList = bigImageWhiteList
    }

    override fun toString(): String {
        val result = StringBuilder()
        result.append(
            """
    <<<<<<<<<<<<<<CompressImagePluginConfig>>>>>>>>>>>>
    
    """.trimIndent()
        )
        result.append(
            """
                maxSize :$maxSize
                isCheckSize: $isCheckSize
                optimizeType: $optimizeType
                enableWhenDebug: $enableWhenDebug
                isCheckPixels: $isCheckPixels
                maxWidth: $maxWidth, maxHeight: $maxHeight
                mctoolsDir: $mctoolsDir
                isSupportAlphaWebp: $isSupportAlphaWebp
                multiThread: $multiThread
                whiteList : 
                
                """.trimIndent()
        )
        for (file in whiteList) {
            result.append("     -> : $file\n")
        }
        result.append("bigImageWhiteList: \n")
        for (file in bigImageWhiteList) {
            result.append("     -> : $file\n")
        }
        result.append("<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>")
        return result.toString()
    }

    companion object {
        const val OPTIMIZE_WEBP_CONVERT = "ConvertWebp" //webp化
        const val OPTIMIZE_COMPRESS_PICTURE = "Compress" //压缩图片
    }
}