package com.lanshifu.compress_image_plugin.utils

import com.lanshifu.compress_image_plugin.CompressImagePluginConst
import java.io.File
import java.io.FileInputStream
import javax.imageio.ImageIO

class ImageUtil {

    companion object {
        private const val SIZE_TAG = "SizeCheck"

        fun isImage(file: File): Boolean {
            return (file.name.endsWith(CompressImagePluginConst.JPG) ||
                    file.name.endsWith(CompressImagePluginConst.PNG) ||
                    file.name.endsWith(CompressImagePluginConst.JPEG)
                    ) && !file.name.endsWith(CompressImagePluginConst.DOT_9PNG)
        }

        fun isJPG(file: File): Boolean {
            return file.name.endsWith(CompressImagePluginConst.JPG) || file.name.endsWith(
                CompressImagePluginConst.JPEG)
        }

        fun isAlphaPNG(filePath: File): Boolean {
            return if (filePath.exists()) {
                try {
                    val img = ImageIO.read(filePath)
                    img.colorModel.hasAlpha()
                } catch (e: Exception) {
                    LogUtil.log(
                        e.message!!
                    )
                    false
                }
            } else {
                false
            }
        }

        fun isBigSizeImage(imgFile: File, maxSize: Float): Boolean {
            if (isImage(
                    imgFile
                )
            ) {
                //判断图片文件大小
                if (imgFile.length() >= maxSize) {
                    LogUtil.log(
                        SIZE_TAG,
                        imgFile.path,
                        true.toString()
                    )
                    return true
                }
            }
            return false
        }

        fun isBigPixelImage(imgFile: File, maxWidth: Int, maxHeight: Int): Boolean {
            if (isImage(
                    imgFile
                )
            ) {
                //读取图片信息，判断宽高
                val sourceImg = ImageIO.read(FileInputStream(imgFile))
                if (sourceImg.height > maxHeight || sourceImg.width > maxWidth) {
                    return true
                }
            }
            return false
        }
    }
}