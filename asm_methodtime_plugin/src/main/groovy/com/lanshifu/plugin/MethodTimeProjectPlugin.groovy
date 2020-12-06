package com.lanshifu.plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

public class MethodTimeProjectPlugin implements Plugin<Project> {
    void apply(Project project) {
        System.out.println("==MethodTimeProjectPlugin gradle plugin==")

        def android = project.extensions.getByType(AppExtension)
        System.out.println '----------- registering MethodTimeProjectPlugin  -----------'
        MethodTimeTransform transform = new MethodTimeTransform()
        android.registerTransform(transform)
    }
}