package com.lanshifu.compress_image_plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.api.BaseVariantImpl
import com.lanshifu.compress_image_plugin.utils.CompressUtil
import com.lanshifu.compress_image_plugin.utils.FileUtil
import com.lanshifu.compress_image_plugin.utils.ImageUtil
import com.lanshifu.compress_image_plugin.utils.LogUtil
import com.lanshifu.compress_image_plugin.utils.Tools
import com.lanshifu.compress_image_plugin.webp.WebpUtils
import com.smallsoho.mcplugin.image.`interface`.IBigImage
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import java.io.File
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future

/**
 *
 * 参考自：
 * https://github.com/smallSohoSolo/McImage/blob/master/README-CN.md
 * @date 2021/1/30
 */
class CompressImagePlugin : Plugin<Project> {

    private lateinit var mProject: Project
    private var mCompressImagePluginConfig: CompressImagePluginConfig = CompressImagePluginConfig()
    private var oldSize: Long = 0
    private var newSize: Long = 0
    val bigImgList = ArrayList<String>()

    var isDebugTask = false
    var isContainAssembleTask = false

    override fun apply(project: Project) {

        mProject = project
        println("CompressImagePlugin->apply")
        //check is library or application
        val hasAppPlugin = project.plugins.hasPlugin("com.android.application")
        val variants = if (hasAppPlugin) {
            (project.property("android") as AppExtension).applicationVariants
        } else {
            (project.property("android") as LibraryExtension).libraryVariants
        }

        //set config
        project.extensions.create(
            "CompressImagePluginConfig",
            CompressImagePluginConfig::class.java
        )
        mCompressImagePluginConfig =
            project.property("CompressImagePluginConfig") as CompressImagePluginConfig

        //判断是debug还是release
        project.gradle.taskGraph.whenReady {
            it.allTasks.forEach { task ->
                val taskName = task.name
                if (taskName.contains("assemble") ||
                    taskName.contains("resguard") ||
                    taskName.contains("bundle")
                ) {
                    if (taskName.toLowerCase().contains("debug")) {
                        isDebugTask = true
                    }
                    isContainAssembleTask = true

                    println("ImagePlugin->isDebugTask=$isDebugTask,isContainAssembleTask=$isContainAssembleTask")

                    return@whenReady //跳出循环
                }
            }
        }

        project.afterEvaluate {
            variants.all { variant ->

                variant as BaseVariantImpl

                checkCompressTools(project)

                val mergeResourcesTask = variant.mergeResourcesProvider.get()
                val compressImageTask = project.task("CompressImagePlugin-${variant.name.capitalize()}")

                compressImageTask.doLast {
                    LogUtil.log("${compressImageTask.name}->doLast")
                    //debug enable
                    if (isDebugTask && !mCompressImagePluginConfig.enableWhenDebug) {
                        LogUtil.log("Debug not run compressImageTask ^_^")
                        return@doLast
                    }

                    //assemble passed
                    if (!isContainAssembleTask) {
                        LogUtil.log("Don't contain assemble task, compressImageTask passed")
                        return@doLast
                    }

                    LogUtil.log("---- Compress Image Plugin Start ----")
                    LogUtil.log(mCompressImagePluginConfig.toString())

                    //获取所有的资源文件目录
                    val dir = variant.allRawAndroidResources.files
                    LogUtil.log("allRawAndroidResources.files.size=${dir.size}")

                    val cacheList = ArrayList<String>()

                    val imageFileList = ArrayList<File>()

                    for (channelDir: File in dir) {
                        traverseResDir(channelDir, imageFileList, cacheList, object : IBigImage {
                            override fun onBigImage(file: File) {
                                bigImgList.add(file.absolutePath)
                            }
                        })
                    }

                    checkBigImage()

                    val start = System.currentTimeMillis()

                    mtDispatchOptimizeTask(imageFileList)
                    LogUtil.log(sizeInfo())
                    LogUtil.log("---- CompressImagePlugin End ----, Total Time(ms) : ${System.currentTimeMillis() - start}")
                }

                //chmod task
                val chmodTaskName = "chmod${variant.name.capitalize()}"
                val chmodTask = project.task(chmodTaskName)
                chmodTask.doLast {
                    LogUtil.log("chmodTaskName=${chmodTaskName} ->doLast")
                    //chmod if linux
                    if (Tools.isLinux()) {
                        Tools.chmod()
                    }
                }

                //inject task
                (project.tasks.findByName(chmodTask.name) as Task).dependsOn(
                    mergeResourcesTask.taskDependencies.getDependencies(
                        mergeResourcesTask
                    )
                )
                (project.tasks.findByName(compressImageTask.name) as Task).dependsOn(
                    project.tasks.findByName(
                        chmodTask.name
                    ) as Task
                )
                mergeResourcesTask.dependsOn(project.tasks.findByName(compressImageTask.name))

            }
        }

    }

    private fun traverseResDir(
        file: File,
        imageFileList: ArrayList<File>,
        cacheList: ArrayList<String>,
        iBigImage: IBigImage
    ) {
        if (cacheList.contains(file.absolutePath)) {
            return
        } else {
            cacheList.add(file.absolutePath)
        }
        if (file.isDirectory) {
            file.listFiles()?.forEach {
                if (it.isDirectory) {
                    traverseResDir(it, imageFileList, cacheList, iBigImage)
                } else {
                    filterImage(it, imageFileList, iBigImage)
                }
            }
        } else {
            filterImage(file, imageFileList, iBigImage)
        }
    }

    private fun filterImage(file: File, imageFileList: ArrayList<File>, iBigImage: IBigImage) {
        if (mCompressImagePluginConfig.whiteList.contains(file.name) || !ImageUtil.isImage(file)) {
            return
        }
        if (((mCompressImagePluginConfig.isCheckSize && ImageUtil.isBigSizeImage(
                file,
                mCompressImagePluginConfig.maxSize
            ))
                    || (mCompressImagePluginConfig.isCheckPixels
                    && ImageUtil.isBigPixelImage(
                file,
                mCompressImagePluginConfig.maxWidth,
                mCompressImagePluginConfig.maxHeight
            )))
            && !mCompressImagePluginConfig.bigImageWhiteList.contains(file.name)
        ) {
            iBigImage.onBigImage(file)
        }
        imageFileList.add(file)
    }

    private fun mtDispatchOptimizeTask(imageFileList: ArrayList<File>) {
        LogUtil.log("mtDispatchOptimizeTask,imageFileList.size=${imageFileList.size} :")
        if (imageFileList.size == 0 || bigImgList.isNotEmpty()) {
//        if (imageFileList.size == 0) {
            return
        }
        val coreNum = Runtime.getRuntime().availableProcessors()
        if (imageFileList.size < coreNum || !mCompressImagePluginConfig.multiThread) {
            for (file in imageFileList) {
                optimizeImage(file)
            }
        } else {
            val results = ArrayList<Future<Unit>>()
            val pool = Executors.newFixedThreadPool(coreNum)
            val part = imageFileList.size / coreNum
            LogUtil.log("multiThread,coreNum=$coreNum,part=$part")
            for (i in 0 until coreNum) {
                val from = i * part
                val to = if (i == coreNum - 1) imageFileList.size - 1 else (i + 1) * part - 1
                results.add(pool.submit(Callable<Unit> {
                    for (index in from..to) {
                        optimizeImage(imageFileList[index])
                    }
                }))
            }
            for (f in results) {
                try {
                    f.get()
                } catch (ignore: Exception) {
                }
            }
        }
    }

    private fun optimizeImage(file: File) {
//        LogUtil.log("optimizeImage:${file.name}")
        val path: String = file.path
        if (File(path).exists()) {
            oldSize += File(path).length()
        }
        when (mCompressImagePluginConfig.optimizeType) {
            CompressImagePluginConfig.OPTIMIZE_WEBP_CONVERT ->
                WebpUtils.securityFormatWebp(file, mCompressImagePluginConfig, mProject)
            CompressImagePluginConfig.OPTIMIZE_COMPRESS_PICTURE ->
                CompressUtil.compressImg(file)
        }
        countNewSize(path)
    }

    private fun countNewSize(path: String) {
        if (File(path).exists()) {
            newSize += File(path).length()
        } else {
            //转成了webp
            val indexOfDot = path.lastIndexOf(".")
            val webpPath = path.substring(0, indexOfDot) + ".webp"
            if (File(webpPath).exists()) {
                newSize += File(webpPath).length()
            } else {
                LogUtil.log("McImage: optimizeImage have some Exception!!!")
            }
        }
    }

    private fun checkBigImage() {
        if (bigImgList.size != 0) {
            val stringBuffer = StringBuffer(
                "You have big Imgages with big size or large pixels," +
                        "please confirm whether they are necessary or whether they can to be compressed. " +
                        "If so, you can config them into bigImageWhiteList to fix this Exception!!!\n"
            )
            for (i: Int in 0 until bigImgList.size) {
                stringBuffer.append(bigImgList[i])
                stringBuffer.append("\n")
            }

            LogUtil.log("大图列表：$stringBuffer")

            throw GradleException(stringBuffer.toString())
        }
    }


    private fun checkCompressTools(project: Project) {
        if (mCompressImagePluginConfig.mctoolsDir.isBlank()) {
            FileUtil.setRootDir(project.rootDir.path)
        } else {
            FileUtil.setRootDir(mCompressImagePluginConfig.mctoolsDir)
        }

        if (!FileUtil.getToolsDir().exists()) {
            FileUtil.getToolsDir().mkdir()
//            throw GradleException("You need put the mctools dir in project root")
        }
    }

    private fun sizeInfo(): String {
        return "->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n" +
                "before McImage optimize: " + oldSize / 1024 + "KB\n" +
                "after McImage optimize: " + newSize / 1024 + "KB\n" +
                "McImage optimize size: " + (oldSize - newSize) / 1024 + "KB\n" +
                "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<-"


    }

}