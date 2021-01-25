package com.gregantech.timepass.network.jsonadapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.JsonReader
import com.squareup.moshi.ToJson

@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class StringToBoolean

class StringToBooleanAdapter {
    @ToJson
    fun toJson(@StringToBoolean value: Boolean): String {
        return when (value) {
            true -> "true"
            false -> "false"
        }
    }

    @FromJson
    @StringToBoolean
    fun fromJson(reader: JsonReader): Boolean {
        val result = if (reader.peek() === JsonReader.Token.NULL) {
            reader.nextNull()
        } else {
            reader.nextString()
        }

        return !result.isNullOrEmpty() &&
                (result.equals("true", true) ||
                        result.equals("YES", true) ||
                        result.equals("Y", true))
    }
}