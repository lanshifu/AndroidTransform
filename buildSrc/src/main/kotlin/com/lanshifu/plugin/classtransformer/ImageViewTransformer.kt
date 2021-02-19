package com.lanshifu.plugin.classtransformer

import com.didiglobal.booster.annotations.Priority
import com.didiglobal.booster.transform.TransformContext
import com.didiglobal.booster.transform.asm.ClassTransformer
import com.didiglobal.booster.transform.asm.className
import com.google.auto.service.AutoService
import com.lanshifu.plugin.DoKitExtUtil
import com.lanshifu.plugin.println
import org.objectweb.asm.tree.ClassNode

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/5/14-18:07
 * 描    述：wiki:https://juejin.im/post/5e8d87c4f265da47ad218e6b
 * 修订历史：
 * ================================================
 */
@Priority(1)
@AutoService(ClassTransformer::class)
class ImageViewTransformer : ClassTransformer {

    private val classNameImageView = "android/widget/ImageView"
    private val classNameImageMonitorImageView = "com/lanshifu/asm_plugin_library/ImageMonitorImageView"

    override fun transform(context: TransformContext, klass: ClassNode): ClassNode {

//        if (!DoKitExtUtil.dokitPluginSwitchOpen()) {
//            return klass
//        }
//
//        if (DoKitExtUtil.ignorePackageNames(klass.className)) {
//            return klass
//        }

        if (klass.superName == classNameImageView && klass.name != classNameImageMonitorImageView) {
            klass.superName = classNameImageMonitorImageView
            "ImageViewTransformer do success".println()
        }

        return klass
    }


}

