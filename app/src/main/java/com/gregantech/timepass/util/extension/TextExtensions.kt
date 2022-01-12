package com.gregantech.timepass.util.extension

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.gregantech.timepass.util.CustomTypefaceSpan

/**
 * @param sizeInDp Integer size to apply for the span.
 * This considers the given size in DP
 * @param start Starting Index of the string.
 * @param end Ending Index of the span + 1.
 */
fun SpannableString.setAbsoluteSpan(
    sizeInDp: Int,
    start: Int,
    end: Int
) {
    this.setSpan(
        AbsoluteSizeSpan(sizeInDp, true),
        start,
        end,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
}

/**
 * This applies the given font and sets a BOLD style.
 *
 * @param fontId Resource ID of the font to apply for the span.
 * @param start Starting Index of the string.
 * @param end Ending Index of the span + 1.
 */
fun SpannableString.setCustomTypefaceSpanBold(
    fontId: Int,
    context: Context,
    start: Int,
    end: Int
) {
    val typeface = Typeface.create(
        ResourcesCompat.getFont(context, fontId),
        Typeface.BOLD
    )

    this.setSpan(
        CustomTypefaceSpan(typeface),
        start,
        end,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
}

/**
 * This applies the given font but does not affect any style.
 *
 * @param fontId Resource ID of the font to apply for the span.
 * @param start Starting Index of the string.
 * @param end Ending Index of the span + 1.
 */
fun SpannableString.setCustomTypefaceSpanNormal(
    fontId: Int,
    context: Context,
    start: Int,
    end: Int
) {
    val typeface = Typeface.create(
        ResourcesCompat.getFont(context, fontId),
        Typeface.NORMAL
    )

    this.setSpan(
        CustomTypefaceSpan(typeface),
        start,
        end,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
}

/**
 * @param colorId Resource ID of the color to apply for the span.
 * @param start Starting Index of the string.
 * @param end Ending Index of the span + 1.
 */
fun SpannableString.setForegroundColorSpan(
    colorId: Int,
    context: Context,
    start: Int,
    end: Int
) {
    val color = ContextCompat.getColor(context, colorId)
    this.setSpan(
        ForegroundColorSpan(color),
        start,
        end,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
}

/**
 * @param typefaceStyle Can be one of Typeface.BOLD, Typeface.NORMAL, Typeface.ITALIC
 * @param start Starting Index of the string.
 * @param end Ending Index of the span + 1.
 */
fun SpannableString.setStyleSpan(
    typefaceStyle: Int,
    start: Int,
    end: Int
) {
    this.setSpan(
        StyleSpan(typefaceStyle),
        start,
        end,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
}