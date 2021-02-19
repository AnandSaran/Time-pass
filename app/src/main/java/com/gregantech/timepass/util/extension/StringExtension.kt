package com.gregantech.timepass.util.extension

import android.util.Patterns
import com.gregantech.timepass.util.constant.*
import java.text.SimpleDateFormat

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

fun CharSequence.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()