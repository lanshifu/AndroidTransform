package com.lanshifu.image_monitor

import org.gradle.api.Project
import org.gradle.api.Plugin

/**
 * @author lanxiaobin
 * @date 2021/1/23
 */
class ImageMonitorPlugin : Plugin<Project> {
    override fun apply(project: Project) {

        //读取配置
        val config = project.extensions.create("ImageMonitor", ImageMonitorConfig::class.java)

        println("ImageMonitorPlugin->apply,$config")
    }
}