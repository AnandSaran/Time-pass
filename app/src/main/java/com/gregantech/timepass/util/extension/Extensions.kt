package com.gregantech.timepass.util.extension


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.webkit.URLUtil
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.gms.ads.AdRequest
import com.gregantech.timepass.BuildConfig
import com.gregantech.timepass.R
import com.gregantech.timepass.model.DownloadResult
import com.gregantech.timepass.util.constant.BODY
import com.gregantech.timepass.util.constant.RAW_DOWNLOAD_PATH
import com.gregantech.timepass.util.constant.SUBJECT
import com.yalantis.ucrop.util.FileUtils.getPath
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.cio.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.io.copyAndClose
import kotlinx.coroutines.withContext
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import java.io.File
import java.net.URLConnection
import kotlin.math.roundToInt

val globalContext: Context
    get() = GlobalContext.get().koin.rootScope.androidContext()


fun Context.shareDownloadedFile(downloadId: Long) {

    val mContext = this
    val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val query = DownloadManager.Query()
    query.setFilterById(downloadId)

    downloadManager.query(query)?.run {
        if (moveToFirst()) {
            val downloadStatus = getInt(getColumnIndex(DownloadManager.COLUMN_STATUS))
            val downloadLocalUri = getString(getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
            val downloadMimeType = getString(getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE))

            Log.d(
                "ShareDownloaded",
                "shareDownloadedFile: downloadMimeType $downloadMimeType downloadLocalUri $downloadLocalUri"
            )

            if (downloadStatus == DownloadManager.STATUS_SUCCESSFUL) {
                shareFile(getPath(mContext, Uri.parse(downloadLocalUri)))
            }
        }
        close()
    }
}

fun String.stripFileNameFromUrl() = substring(lastIndexOf("/") + 1)

fun Context.isFileDownloaded(fileName: String?) =
    File(RAW_DOWNLOAD_PATH.plus(fileName)).exists()

fun Context.shareFile(filePath: String?, mimeType: String? = null) {

    filePath?.let { path ->
        val file = File(path)
        if (!file.exists())
            return

        val uri = FileProvider.getUriForFile(this, "${BuildConfig.APPLICATION_ID}.provider", file)
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = mimeType ?: uri.getMimeTypeForIntent()
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, SUBJECT)
            putExtra(Intent.EXTRA_TEXT, BODY)
        }
        startActivity(Intent.createChooser(shareIntent, "Share using"))
    }
}


fun Uri.getMimeTypeForIntent() = this.toString().run {

    val fileType = URLConnection.guessContentTypeFromName(this)
    Log.d("Ext", "getMimeTypeForIntent: guessed fileType $fileType")
    if (!fileType.isNullOrEmpty()) {
        return fileType
    }

    when {
        contains(".doc", ignoreCase = true) || contains(
            ".docx",
            ignoreCase = true
        ) -> "application/msword"
        contains(".pdf", ignoreCase = true) -> "application/pdf"
        contains(".ppt", ignoreCase = true) || contains(
            ".pptx",
            ignoreCase = true
        ) -> "application/vnd.ms-powerpoint"
        contains(".xls", ignoreCase = true) || contains(
            ".xlsx",
            ignoreCase = true
        ) -> "application/vnd.ms-excel"
        contains(".zip", ignoreCase = true) || contains(
            ".rar",
            ignoreCase = true
        ) -> "application/x-wav"
        contains(".rtf", ignoreCase = true) -> "application/rtf"
        contains(".wav", ignoreCase = true) || contains(".mp3", ignoreCase = true) -> "audio/x-wav"
        contains(".gif", ignoreCase = true) -> "image/gif"
        contains(".jpg") || contains(".jpeg", ignoreCase = true) || contains(
            ".png",
            ignoreCase = true
        ) -> "image/jpeg"
        contains(".txt", ignoreCase = true) -> "text/plain"
        contains(".3gp", ignoreCase = true) || contains(
            ".mpg",
            ignoreCase = true
        ) || contains(".mpeg", ignoreCase = true) || contains(".mpe", ignoreCase = true) ||
                contains(".mp4", ignoreCase = true) || contains(
            ".avi",
            ignoreCase = true
        ) || contains(".mov", ignoreCase = true) || contains(".mkv", ignoreCase = true) -> "video/*"
        else -> "*.*"
    }
}

suspend fun HttpClient.downloadFile(
    file: File,
    url: String,
    callback: suspend (boolean: Boolean) -> Unit
) {
    val call = call {
        url(url)
        method = HttpMethod.Get
    }
    if (!call.response.status.isSuccess()) {
        callback(false)
    }
    call.response.content.copyAndClose(file.writeChannel())
    callback(true)
}

suspend fun HttpClient.downloadFile(file: File, url: String): Flow<DownloadResult> {
    return flow {
        val response = call {
            url(url)
            method = HttpMethod.Post
        }.response
        val data = ByteArray(response.contentLength()!!.toInt())
        var offset = 0
        do {
            val currentRead = response.content.readAvailable(data, offset, data.size)
            offset += currentRead
            val progress = (offset * 100f / data.size).roundToInt()
            emit(DownloadResult.Progress(progress))
        } while (currentRead > 0)
        response.close()
        if (response.status.isSuccess()) {
            file.writeBytes(data)
            emit(DownloadResult.Success)
        } else {
            emit(DownloadResult.Error("File not downloaded"))
        }
    }
}

// -> replace/add your deviceId
val testDeviceList = arrayListOf(AdRequest.DEVICE_ID_EMULATOR, "2F14BC616837FB4F44DA919217E0A95A")

fun Activity.openFile(file: File) {
    Intent(Intent.ACTION_VIEW).apply {
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        addCategory(Intent.CATEGORY_DEFAULT)
        val uri = FileProvider.getUriForFile(
            this@openFile,
            BuildConfig.APPLICATION_ID + ".provider",
            file
        )
        val mimeType = getMimeType(file)
        mimeType?.let {
            setDataAndType(uri, it)
            startActivity(this)
        }

    }
}

fun RecyclerView.horizontalView(context: Context) {
    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
}

fun getMimeType(file: File): String? {
    val extension = file.extension
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
}

fun Context.openWebLink(link: String) {
    if (URLUtil.isValidUrl(link)) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
    }
}


fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.INVISIBLE
}

fun View.isVisible() = visibility == View.VISIBLE

fun View.gone() {
    visibility = View.GONE
}

fun View.animShow() {
    animate().alpha(1f).setDuration(300).setListener(object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator) {
            super.onAnimationStart(animation)
            visibility = View.VISIBLE
        }
    })
}

fun View.animGone() {
    animate().alpha(0f).setDuration(300).setListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            visibility = View.GONE
        }
    })
}

fun Drawable.toBitmap(): Bitmap {
    if (this is BitmapDrawable) {
        return this.bitmap
    }

    val bitmap =
        Bitmap.createBitmap(this.intrinsicWidth, this.intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    this.setBounds(0, 0, canvas.width, canvas.height)
    this.draw(canvas)

    return bitmap
}

suspend fun Context.clearCache() = withContext(Dispatchers.IO) {
    val state = deleteDir(File("${cacheDir.absolutePath}/${getString(R.string.app_name)}"))
    Log.d("FileExt", "clearCache: is Cleared $state")
}

fun deleteDir(dir: File?): Boolean {
    runCatching {
        //Log.d("FileExt", "deleteDir: dir $dir")
        return if (dir != null && dir.isDirectory) {
            val children = dir.list()
            if (children.isNullOrEmpty())
                return false
            for (i in children.indices) {
                val success: Boolean = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
            dir.delete()
        } else if (dir != null && dir.isFile) {
            dir.delete()
        } else {
            false
        }
    }.onFailure {
        return false
    }
    return false
}

var Player.muted: Boolean
    get() {
        return (this as SimpleExoPlayer).volume == 0.0f
    }
    set(value) {
        (this as SimpleExoPlayer).volume = if (value) 0.0f else 1.0f
    }

