package com.example.weather.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.LinearGradient
import android.graphics.Shader
import android.util.AttributeSet
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.weather.R

class GradiantTextVView : androidx.appcompat.widget.AppCompatTextView {
    constructor(context: Context) :super(context)

    constructor(context: Context,attrs:AttributeSet) :super(context,attrs)
    constructor(context: Context,attrs:AttributeSet,defStyleAttr: Int) :super(context,attrs,defStyleAttr)

    @SuppressLint("DrawAllocation")
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed){
            paint.shader = LinearGradient(0f,width.toFloat(),height.toFloat(),0f, intArrayOf(
                ContextCompat.getColor(context, R.color.lightblue),
                ContextCompat.getColor(context, R.color.white),
                ContextCompat.getColor(context, R.color.white),
            ),null,
                Shader.TileMode.CLAMP
            )
        }
    }
}


