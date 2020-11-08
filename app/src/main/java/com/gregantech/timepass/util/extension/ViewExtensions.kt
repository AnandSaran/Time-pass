package com.gregantech.timepass.util.extension

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.widget.*
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import com.bumptech.glide.Glide

/**
 * Created by anand
 */

fun View.visible(visible: Boolean, useGone: Boolean = true) {
    this.visibility = if (visible) View.VISIBLE else if (useGone) View.GONE else View.INVISIBLE
}

fun ImageView.loadUrl(url: String) {
    if (url.isNotBlank())
        Glide.with(context)
            .load(url)
            .into(this)
}

fun View.loadDrawableBackground(@DrawableRes drawable: Int) {
    ContextCompat.getDrawable(
        context,
        drawable
    )?.let { bg ->
        this.background = bg
    }
}

fun ImageView.loadDrawable(@DrawableRes drawable: Int) {
    ContextCompat.getDrawable(
        context,
        drawable
    )?.let { image ->
        Glide.with(context)
            .load(image)
            .into(this)
    }
}

fun ImageView.applyTint(@ColorRes colorRes: Int) {
    ImageViewCompat.setImageTintList(
        this,
        ColorStateList.valueOf(ContextCompat.getColor(context, colorRes))
    )
}

fun ImageView.loadDrawable(drawable: Drawable) {
    Glide.with(context)
        .load(drawable)
        .into(this)
}

fun TextView.applyText(
    @StringRes prefix: Int = -1,
    @StringRes textResID: Int,
    @StringRes suffix: Int = -1
) {
    var prefixText = ""
    var suffixText = ""
    val wordText = context.getString(textResID)

    if (prefix != -1) {
        prefixText = context.getString(prefix)
    }

    if (suffix != -1) {
        suffixText = context.getString(suffix)
    }

    this.text = "$prefixText$wordText$suffixText"
}

fun TextView.applyText(
    @StringRes prefix: Int = -1,
    wordText: String,
    @StringRes suffix: Int = -1
) {
    var prefixText = ""
    var suffixText = ""
    if (prefix != -1) {
        prefixText = context.getString(prefix)
    }

    if (suffix != -1) {
        suffixText = context.getString(suffix)
    }

    this.text = "$prefixText$wordText$suffixText"
}

fun TextView.applyTextColor(@ColorRes color: Int) {
    this.setTextColor(ContextCompat.getColor(context, color))
}

fun ProgressBar.setTintColor(@ColorRes color: Int) {
    this.progressDrawable.setTint(
        ContextCompat.getColor(
            context,
            color
        )
    )
}

fun Any.toast(context: Context, duration: Int = Toast.LENGTH_SHORT): Toast {
    return Toast.makeText(context, this.toString(), duration).apply { show() }
}

fun TextView.leftDrawable(@DrawableRes id: Int = 0) {
    this.setCompoundDrawablesWithIntrinsicBounds(id, 0, 0, 0)
}

fun EditText.contentText(): String {
    return this.text.toString()
}

fun EditText.onChange(cb: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            cb(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
}