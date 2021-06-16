package com.gregantech.timepass.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.ViewSwitcher

class TimePassViewSwitcher : ViewSwitcher {

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(
        context: Context?, attrs: AttributeSet?
    ) : super(context, attrs) {
        init()
    }

    private fun init() {
    }

    fun setChildVisible() {
        if (displayedChild != 1) {
            displayedChild = 1
        }
    }

    fun setParentVisible() {
        if (displayedChild != 0) {
            displayedChild = 0
        }
    }
}