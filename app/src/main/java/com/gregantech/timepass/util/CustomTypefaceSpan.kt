package com.gregantech.timepass.util

import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.MetricAffectingSpan

class CustomTypefaceSpan(private val typeFace: Typeface?) : MetricAffectingSpan() {
    private val WRONGTYPEFACE = 0
    override fun updateDrawState(paint: TextPaint) {
        updateTypeface(paint)
    }

    override fun updateMeasureState(paint: TextPaint) {
        updateTypeface(paint)
    }

    private fun updateTypeface(textPaint: TextPaint) {
        textPaint.apply {
            val oldStyle = getOldStyle(typeFace)

            if (oldStyle == WRONGTYPEFACE) return

            typeface = Typeface.create(typeFace, oldStyle)
        }
    }

    private fun getOldStyle(typeface: Typeface?) = typeface?.style ?: WRONGTYPEFACE

}