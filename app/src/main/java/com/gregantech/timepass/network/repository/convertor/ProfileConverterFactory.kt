package com.gregantech.timepass.network.repository.convertor

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toFile
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import java.io.File

class ProfileConverterFactory(private val context: Context) {

    suspend fun getCompressedImage(filePath: Uri): File {
        return Compressor.compress(context, filePath.toFile()){
            default(width = 640, format = Bitmap.CompressFormat.PNG)
        }
    }
}