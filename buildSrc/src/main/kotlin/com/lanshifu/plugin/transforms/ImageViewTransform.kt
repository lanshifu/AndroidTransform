package com.lanshifu.plugin.transforms

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.lanshifu.plugin.base.BaseTransform
import com.lanshifu.plugin.base.ClassUtils
import com.lanshifu.plugin.base.TransformCallBack
import com.lanshifu.plugin.visitors.ImageViewAsmHelper
import org.gradle.api.Project

/**
 * @Author LiABao
 * @Since 2020/10/12
 */
class ImageViewTransform(private val project: Project) : Transform() {
    override fun getName(): String {
        return "AutoSizeTransform"
    }

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_JARS
    }

    override fun isIncremental(): Boolean {
        return true
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    override fun transform(transformInvocation: TransformInvocation?) {
        val helper = ImageViewAsmHelper()
        val baseTransform = BaseTransform(transformInvocation, object : TransformCallBack {
            override fun process(className: String, classBytes: ByteArray?): ByteArray? {
                return if (ClassUtils.checkClassName(className)) {
                    helper.modifyClass(classBytes)
                } else {
                    null
                }
            }
        })
        baseTransform.startTransform()
    }
}