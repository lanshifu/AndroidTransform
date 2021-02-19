package com.lanshifu.compress_image_plugin.utils

import java.io.File

object FileUtil {

    private lateinit var rootDir: String

    fun setRootDir(rootDir: String) {
        FileUtil.rootDir = rootDir
    }

    fun getRootDirPath(): String {
        return rootDir
    }

    fun getToolsDir(): File {
        return File("$rootDir/mctools/")
    }

    fun getToolsDirPath(): String {
        return "$rootDir/mctools/"
    }
}
