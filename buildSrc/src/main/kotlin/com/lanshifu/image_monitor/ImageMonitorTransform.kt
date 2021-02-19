package com.lanshifu.image_monitor

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.compress.utils.IOUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassReader.EXPAND_FRAMES
import org.objectweb.asm.ClassWriter
import java.io.File
import java.io.FileOutputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

/**
 * @author lanxiaobin
 * @date 2021/1/30
 */
class ImageMonitorTransform : Transform() {
    override fun getName(): String {
        return "ImageMonitorTransform"
    }

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        //class 代表只检索.class文件
        //resource 检索java标准的资源文件
        return TransformManager.CONTENT_CLASS
    }

    override fun isIncremental(): Boolean {
        //是否支持增量编译
        return false
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        //检索范围
        return TransformManager.SCOPE_FULL_PROJECT
    }

    override fun transform(transformInvocation: TransformInvocation) {
        //拿到所有的class文件
        val transformInputs = transformInvocation.inputs
        //下一个对象
        val outputProvider = transformInvocation.outputProvider
        //删除上一个
        outputProvider?.deleteAll()

        transformInputs.forEach { transformInput ->
            // 遍历directoryInputs(文件夹中的class文件) directoryInputs代表着以源码方式参与项目编译的所有目录结构及其目录下的源码文件
            // 比如我们手写的类以及R.class、BuildConfig.class以及MainActivity.class等
            transformInput.directoryInputs.forEach { directoryInput ->
                handleDirectoryInput(directoryInput, outputProvider)
            }

            transformInput.jarInputs.forEach { jarInput ->
                handleJarInput(jarInput, outputProvider)
            }
        }


    }

    private fun handleDirectoryInput(
        directoryInput: DirectoryInput,
        outputProvider: TransformOutputProvider
    ) {
        if (directoryInput.file.isDirectory()) {
            directoryInput.file.listFiles().forEach { file ->
                val name = file.name

                if (filterClass(name)) {
                    val classReader = ClassReader(file.readBytes())
                    val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)

                    //asm常用操作写逻辑
                    val cv = ImageMonitorClassVisitor(classWriter)
                    classReader.accept(cv, EXPAND_FRAMES)

                    val code = classWriter.toByteArray()
                    val fos = FileOutputStream(file.parentFile.absolutePath + File.separator + name)
                    fos.write(code)
                    fos.close()
                }
            }
        }

        //处理完输出给下一任务作为输入
        val dest = outputProvider.getContentLocation(
            directoryInput.name,
            directoryInput.contentTypes, directoryInput.scopes,
            Format.DIRECTORY
        )
        FileUtils.copyDirectory(directoryInput.file, dest)

    }

    private fun filterClass(name: String): Boolean {
        if (!name.endsWith(".class")){
            return false
        }
        return true
    }

    private fun handleJarInput(jarInput: JarInput, outputProvider: TransformOutputProvider) {
        if (jarInput.file.absolutePath.endsWith(".jar")) {

            //重命名输出文件,因为可能同名,会覆盖
            var jarName = jarInput.name
            val md5Name = DigestUtils.md5Hex(jarInput.file.absolutePath)
            if (jarName.endsWith(".jar")) {
                jarName = jarName.substring(0, jarName.length - 4)
            }
            val jarFile = JarFile(jarInput.file)
            val enumeration = jarFile.entries()

            val tmpFile = File(jarInput.file.parent + File.separator + "classes_temp.jar")
            if (tmpFile.exists()) {
                tmpFile.delete()
            }

            val jarOutputStream = JarOutputStream(FileOutputStream(tmpFile))

//            println("handleJarInput->start,jarName=$jarName,md5Name=$md5Name")

            //循环jar包里的文件
            while (enumeration.hasMoreElements()) {
                val jarEntry = enumeration.nextElement() as JarEntry
                val entryName = jarEntry.getName()
                val zipEntry = java.util.zip.ZipEntry(entryName)
                val inputStream = jarFile.getInputStream(jarEntry)
                if (filterClass(entryName)) {
                    jarOutputStream.putNextEntry(zipEntry)
                    val classReader = ClassReader(IOUtils.toByteArray(inputStream))
                    val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                    val cv = ImageMonitorClassVisitor(classWriter)
                    classReader.accept(cv, EXPAND_FRAMES)
                    val code = classWriter.toByteArray()
                    jarOutputStream.write(code)
                } else {
                    jarOutputStream.putNextEntry(zipEntry)
                    jarOutputStream.write(IOUtils.toByteArray(inputStream))
                }
                jarOutputStream.closeEntry()
            }

            jarOutputStream.close()
            jarFile.close()

            //处理完输出给下一任务作为输入
            val dest = outputProvider.getContentLocation(
                jarName + md5Name, jarInput.contentTypes, jarInput.scopes, Format.JAR
            )
            FileUtils.copyFile(tmpFile, dest)
            tmpFile.delete()

//            println("handleJarInput->end")
        }
    }
}
