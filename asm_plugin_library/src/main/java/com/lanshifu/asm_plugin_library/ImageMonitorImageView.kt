package com.lanshifu.asm_plugin_library

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.widget.ImageView

/**
 * @author lanxiaobin
 * @date 2021/1/30
 */
open class ImageMonitorImageView : ImageView {

    private val TAG = "ImageMonitorImageView"

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )


    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)

        Log.i(TAG, "setImageDrawable: ")
    }


    override fun setBackground(background: Drawable?) {
        super.setBackground(background)
        Log.e(TAG, "setBackground: ")
    }
}