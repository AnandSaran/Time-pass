package com.gregantech.timepass.util.extension

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.gregantech.timepass.R
import com.gregantech.timepass.widget.AnimateCounter
import com.klinker.android.link_builder.Link
import com.klinker.android.link_builder.applyLinks
import java.io.File

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

fun ImageView.loadUri(uri: Uri) {
    Glide.with(context)
        .load(uri)
        .into(this)
}

fun ImageView.loadUriCircle(uri: Uri) {
    Glide.with(context)
        .load(uri)
        .circleCrop()
        .into(this)
}

fun ImageView.loadUrlCircle(url: String?, placeHolder: Int = R.drawable.logo_app_icon) {
    Glide.with(context)
        .load(url)
        .circleCrop()
        .apply {
            placeholder(placeHolder)
            error(placeHolder)
        }
        .into(this)
}

fun ImageView.setImageRoundedCorner(
    imgUrl: Any?,
    placeholder: Int = R.drawable.place_holder_profile
) {

    val options = RequestOptions()
        .placeholder(placeholder)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .transform(FitCenter(), RoundedCorners(10))
        .priority(Priority.HIGH)

    Glide.with(this.context)
        .load(imgUrl)
        .apply(options)
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

fun Context.shareVideoText(url: String) {
    val shareIntent = Intent()
    shareIntent.action = Intent.ACTION_SEND
    shareIntent.type = "text/plain"
    shareIntent.putExtra(Intent.EXTRA_TEXT, url)
    startActivity(
        Intent.createChooser(
            shareIntent,
            getString(R.string.app_name)
        )
    )
}

fun Context.shareVideoText(file: File) {
    val shareIntent = Intent()
    shareIntent.action = Intent.ACTION_SEND
    shareIntent.type = "video/*"
    shareIntent.putExtra(Intent.EXTRA_STREAM, file.toURI())
    startActivity(
        Intent.createChooser(
            shareIntent,
            getString(R.string.app_name)
        )
    )
}

fun Activity.makeStatusBarTransparent() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
            statusBarColor = Color.TRANSPARENT
        }
    }
}

fun View.setMarginTop(marginTop: Int) {
    val menuLayoutParams = this.layoutParams as ViewGroup.MarginLayoutParams
    menuLayoutParams.setMargins(0, marginTop, 0, 0)
    this.layoutParams = menuLayoutParams
}

fun RecyclerView.smoothSnapToPosition(
    position: Int,
    snapMode: Int = LinearSmoothScroller.SNAP_TO_START
) {
    val smoothScroller = object : LinearSmoothScroller(this.context) {
        override fun getVerticalSnapPreference(): Int = snapMode
        override fun getHorizontalSnapPreference(): Int = snapMode
    }
    smoothScroller.targetPosition = position
    layoutManager?.startSmoothScroll(smoothScroller)
}

fun AppCompatTextView.applyTextHyperLink(
    entries: Array<CharSequence>,
    linkTextColor: Int = R.color.colorAccent,
    isUnderLined: Boolean = true,
    hyperLinkTextFont: Int = R.font.roboto,
    onItemClicked: (String?) -> Unit
) {
    val listOfHyperLinks = arrayListOf<Link>()

    entries.forEach { txt ->
        val link = Link(txt.toString())
            .setTextColor(ContextCompat.getColor(context, linkTextColor))
            .setUnderlined(isUnderLined)
            .setTypeface(ResourcesCompat.getFont(context, hyperLinkTextFont)!!)
            .setOnClickListener {
                onItemClicked.invoke(it)
            }
        listOfHyperLinks.add(link)
    }

    if (listOfHyperLinks.isNotEmpty()) {
        this.applyLinks(listOfHyperLinks)
    }
}

fun AppCompatTextView.fadeIn(title: String, context: Context?) {
    context?.let {
        val anim: Animation = AnimationUtils.loadAnimation(it, R.anim.fade_in)
        text = title
        startAnimation(anim)
    }

}

fun AppCompatTextView.anim(title: String) {
    val from = text.toString().toFloat()
    val to = title.toFloat()
    if (from == to)
        return
    AnimateCounter.Builder(this)
        .setCount(from, to, 0)
        .setDuration(1000)
        .setInterpolator(AccelerateDecelerateInterpolator())
        .build()
        .execute()
}