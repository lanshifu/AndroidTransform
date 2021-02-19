package com.lanshifu.image_monitor

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes

/**
 * ClassVisitor，负责访问.class文件中各个元素，还记得上一课时我们介绍的.class文件结构吗？
 * ClassVisitor就是用来解析这些文件结构的，当解析到某些特定结构时（比如类变量、方法），
 * 它会自动调用内部相应的 FieldVisitor 或者 MethodVisitor 的方法，进一步解析或者修改 .class 文件内容
 */
class ImageMonitorClassVisitor(cv: ClassVisitor?) : ClassVisitor(Opcodes.ASM5, cv) {

    companion object {
        const val ImageView = "android/widget/ImageView"
        const val ImageMonitorImageView = "com/lanshifu/asm_plugin_library/ImageMonitorImageView"
    }
    override fun visit(
        version: Int,
        access: Int,
        name: String,
        signature: String?,
        superName: String,
        interfaces: Array<String>
    ) {
        var mSuperName = superName

        if (mSuperName == ImageView && name != ImageMonitorImageView) {
            println("ImageMonitorClassVisitor->get,className=$name,superName=$mSuperName")
            mSuperName = ImageMonitorImageView
        }
        super.visit(version, access, name, signature, mSuperName, interfaces)
    }

}