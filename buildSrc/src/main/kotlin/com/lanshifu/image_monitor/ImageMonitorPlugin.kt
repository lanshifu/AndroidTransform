package com.lanshifu.image_monitor

import com.android.build.gradle.AppExtension
import org.gradle.api.Project
import org.gradle.api.Plugin

/**
 * @author lanxiaobin
 * @date 2021/1/23
 */
class ImageMonitorPlugin : Plugin<Project> {
    override fun apply(project: Project) {

        //读取配置
        val config = project.extensions.create("ImageMonitorConfig", ImageMonitorConfig::class.java)

        println("ImageMonitorPlugin->apply,$config")

        val android = project.extensions.getByType(AppExtension::class.java)
        val transform = ImageMonitorTransform()
        android.registerTransform(transform)

    }
}