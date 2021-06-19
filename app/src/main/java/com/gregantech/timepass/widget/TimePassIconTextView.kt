package com.gregantech.timepass.widget

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.gregantech.timepass.R
import com.gregantech.timepass.util.extension.anim
import com.gregantech.timepass.util.extension.gone
import com.gregantech.timepass.util.extension.show
import kotlinx.android.synthetic.main.widget_timepass_icon_textview.view.*

class TimePassIconTextView : ConstraintLayout {

    lateinit var view: View
    private var isIconShown = true

    constructor(context: Context) : super(context)

    constructor(context: Context, attrSet: AttributeSet) : super(context, attrSet) {
        init(attrSet)
    }

    constructor(context: Context, attrSet: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrSet,
        defStyleAttr
    ) {
        init(attrSet)
    }

    private fun init(attrSet: AttributeSet) {
        view = LayoutInflater.from(context).inflate(R.layout.widget_timepass_icon_textview, this)
        val typedArray = context.obtainStyledAttributes(attrSet, R.styleable.TimePassIconTextView)
        assignProps(typedArray)
        typedArray.recycle()
    }

    fun setLabel(value: String?) {
        value?.let {
            view.tpItv_label.anim(it)
            invalidate()
        }
    }

    private fun assignProps(typedArray: TypedArray) {
        with(typedArray) {
            getResourceId(R.styleable.TimePassIconTextView_tpBg, -1).let {
                if (it != -1)
                    view.tpItv_rootView.background = ContextCompat.getDrawable(context, it)
            }
            getBoolean(R.styleable.TimePassIconTextView_showIcon, true).let {
                isIconShown = it
                view.tpItv_pic.apply {
                    if (it) show() else gone()
                }
            }
            getResourceId(R.styleable.TimePassIconTextView_tpIcon, -1).let {
                if (it != -1)
                    view.tpItv_pic.setImageResource(it)
            }
            getString(R.styleable.TimePassIconTextView_tpLabel)?.let {
                view.tpItv_label.apply {
                    if (!isIconShown) {
                        width = resources.getDimensionPixelOffset(R.dimen.dp_50)
                        gravity = Gravity.CENTER
                    }
                    text = it
                }
            }
            getBoolean(R.styleable.TimePassIconTextView_tpLabelCapsAll, false).let {
                if (it) {
                    view.tpItv_label.isAllCaps = true
                }
            }
        }
    }


}