package com.lanshifu.plugin.transform

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.build.gradle.internal.pipeline.TransformManager.SCOPE_FULL_PROJECT
import com.didiglobal.booster.gradle.SCOPE_FULL_WITH_FEATURES
import com.didiglobal.booster.gradle.SCOPE_PROJECT
import com.didiglobal.booster.gradle.getAndroid
import com.didiglobal.booster.transform.AbstractKlassPool
import com.didiglobal.booster.transform.Transformer
import org.gradle.api.Project
import java.util.*

/**
 * Represents the transform base
 * DoKitCommTransform 作用于 CommTransformer、BigImgTransformer、UrlConnectionTransformer、GlobalSlowMethodTransformer
 * @author johnsonlee
 */
open class DoKitBaseTransform(val project: Project) : Transform() {

    /*
     * Preload transformers as List to fix NoSuchElementException caused by ServiceLoader in parallel mode
     */
    internal open val transformers = ServiceLoader.load(Transformer::class.java, javaClass.classLoader).toList()

    private val androidExt: BaseExtension = project.getAndroid()

    private lateinit var androidKlassPool: AbstractKlassPool

    init {
        project.afterEvaluate {
            androidKlassPool = object : AbstractKlassPool(androidExt.bootClasspath) {}
        }
    }

    val bootKlassPool: AbstractKlassPool
        get() = androidKlassPool

    override fun getName() = this.javaClass.simpleName

    override fun isIncremental() = true

    override fun isCacheable() = true

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> = TransformManager.CONTENT_CLASS

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> = when {
        transformers.isEmpty() -> mutableSetOf()
        project.plugins.hasPlugin("com.android.library") -> SCOPE_PROJECT
        project.plugins.hasPlugin("com.android.application") -> SCOPE_FULL_PROJECT
        project.plugins.hasPlugin("com.android.dynamic-feature") -> SCOPE_FULL_WITH_FEATURES
        else -> TODO("Not an Android project")
    }


    override fun getReferencedScopes(): MutableSet<in QualifiedContent.Scope> {
        if (transformers.isEmpty()) {
            return when {
                project.plugins.hasPlugin("com.android.library") -> SCOPE_PROJECT
                project.plugins.hasPlugin("com.android.application") -> SCOPE_FULL_PROJECT
                project.plugins.hasPlugin("com.android.dynamic-feature") -> SCOPE_FULL_WITH_FEATURES
                else -> TODO("Not an Android project")
            }
        }
        return super.getReferencedScopes()
    }

    final override fun transform(invocation: TransformInvocation) {
        DoKitTransformInvocation(invocation, this).apply {
            if (isIncremental) {
                doIncrementalTransform()
            } else {
                outputProvider?.deleteAll()
                doFullTransform()
            }
        }
    }

}
