package com.gregantech.timepass.util.extension

import android.util.Patterns
import com.gregantech.timepass.util.constant.*
import org.ocpsoft.prettytime.PrettyTime
import java.text.SimpleDateFormat
import java.util.*

fun String.appendPost(): String {
    return if (this.isBlank()) {
        ZERO.plus(POST)
    } else {
        this.plus(POST)
    }
}


fun String.appendPostText(): String {
    return if (this.isBlank()) {
        POST
    } else {
        this.plus(SINGLE_WHITE_SPACE).plus(POST)
    }
}

fun String.appendFollowers(): String {
    return if (this.isBlank()) {
        ZERO.plus(FOLLOWERS)
    } else {
        this.plus(FOLLOWERS)
    }
}

fun String.appendFollowing(): String {
    return if (this.isBlank()) {
        ZERO.plus(FOLLOWING)
    } else {
        this.plus(FOLLOWING)
    }
}

fun String.toPrettyTime(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
    sdf.timeZone = TimeZone.getDefault()
    val date = sdf.parse(this)
    date?.let {
        return Calendar.getInstance().apply {
            time = it
        }.timeInMillis.getPrettyTime()
    }
    return ""
}

fun Long.getPrettyTime(): String {
    return when (val mDate = PrettyTime(Locale.ENGLISH).format(Date(this))) {
        "moments from now", "moments ago" -> "just now"
        else -> mDate
    }
}

fun String.toTime(): String {
    return when {
        this.isBlank() -> {
            EMPTY_STRING
        }
        this == JUST_NOW -> {
            JUST_NOW
        }
        else -> {
            val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val formatter = SimpleDateFormat("dd/MM/yyyy hh:mm a")
            val output: String = formatter.format(parser.parse(this))
            output

        }
    }
}

fun CharSequence.isValidEmail() =
    if (isNullOrEmpty()) true else Patterns.EMAIL_ADDRESS.matcher(this).matches()