package com.lanshifu.plugin

import com.android.build.gradle.AppExtension
import com.lanshifu.plugin.transforms.ImageViewTransform
import org.gradle.api.Project
import org.gradle.api.Plugin

/**
 * @author lanxiaobin
 * @date 2021/1/23
 */
class ImageViewPlugin : Plugin<Project> {
    override fun apply(project: Project) {

        //读取配置
//        val config = project.extensions.create("ImageMonitorConfig", ImageMonitorConfig::class.java)

        println("ImageViewPlugin->apply")

        val android = project.extensions.getByType(AppExtension::class.java)
        val transform = ImageViewTransform(project)
        android.registerTransform(transform)

    }
}