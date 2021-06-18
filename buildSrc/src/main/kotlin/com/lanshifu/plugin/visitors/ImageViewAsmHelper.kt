package com.lanshifu.plugin.visitors

import com.lanshifu.plugin.base.AsmHelper
import com.lanshifu.plugin.base.Log.info
import com.lanshifu.plugin.base.utils.find
import com.lanshifu.plugin.base.utils.isInstanceOf
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.tree.TypeInsnNode


class ImageViewAsmHelper : AsmHelper {

    companion object {
        const val ImageView = "android/widget/ImageView"
        const val AppCompatImageView = "androidx/appcompat/widget/AppCompatImageView"
        const val ImageMonitorImageView = "com/lanshifu/asm_plugin_library/ImageMonitorImageView"

    }

    override fun modifyClass(srcClass: ByteArray?): ByteArray {
        val classNode = ClassNode(Opcodes.ASM5)
        val classReader = ClassReader(srcClass)
        //1 将读入的字节转为classNode
        classReader.accept(classNode, 0)
        //2 对classNode的处理逻辑
        info("ImageViewAsmHelper modifyClass name = ${classNode.name}")
        val iterator: Iterator<MethodNode> = classNode.methods.iterator()
        while (iterator.hasNext()) {
            val method = iterator.next()
            method.instructions?.iterator()?.forEach {
                when (it.opcode) {
                    Opcodes.NEW -> {
                        if (it is TypeInsnNode) {
                            //(it.desc == ImageView || it.desc == AppCompatImageView)
                            info("ImageViewAsmHelper transformNew,desc=${it.desc}")
//                            it.transformNew(classNode, method)
                        }

                    }

                    Opcodes.INVOKESPECIAL -> {
                        if (it is MethodInsnNode) {
                            if (classNode.name != ImageMonitorImageView && it.owner == ImageView && it.name== "<init>"){
                                info("ImageViewAsmHelper owner=${it.owner},name=${it.name},desc=${it.desc},classNode.name=${classNode.name}")
                                it.owner = ImageMonitorImageView
                            }

                        }
                    }
                }
            }
        }

        val classWriter = ClassWriter(0)
        //3  将classNode转为字节数组
        classNode.accept(classWriter)
        return classWriter.toByteArray()
    }

    private fun TypeInsnNode.transformNew(klass: ClassNode, method: MethodNode) {
        this.find {
            (it.opcode == Opcodes.INVOKESPECIAL) &&
                    (it is MethodInsnNode) &&
                    (this.desc == it.owner && "<init>" == it.name)
        }?.isInstanceOf { init: MethodInsnNode ->

            this.desc = ImageMonitorImageView
            //INVOKESPECIAL androidx/appcompat/app/AlertDialog$Builder.<init> (Landroid/content/Context;)V
            init.apply {
//                if (this.desc == "(Landroid/content/Context;)V") {
                    owner = ImageMonitorImageView
                    info("ImageViewAsmHelper transformNew,className=${klass.name},methodName=${method.name},method.desc=${method.desc},owner = ${init.owner}")
//                }
            }
        }

    }


}