package com.lanshifu.plugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import com.android.build.gradle.AppExtension

public class QuickClickPlugin implements Plugin<Project> {
    void apply(Project project) {
        System.out.println("==LifeCyclePlugin gradle plugin==")

        def android = project.extensions.getByType(AppExtension)
        System.out.println '----------- registering AutoTrackTransform  -----------'
        QuickClickTransform transform = new QuickClickTransform()
        android.registerTransform(transform)
    }
}