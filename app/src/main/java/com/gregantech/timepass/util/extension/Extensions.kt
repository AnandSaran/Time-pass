package com.gregantech.timepass.util.extension


import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.gregantech.timepass.BuildConfig
import com.gregantech.timepass.model.DownloadResult
import com.gregantech.timepass.util.constant.BODY
import com.gregantech.timepass.util.constant.SUBJECT
import com.yalantis.ucrop.util.FileUtils.getPath
import io.ktor.client.HttpClient
import io.ktor.client.call.call
import io.ktor.client.request.url
import io.ktor.http.HttpMethod
import io.ktor.http.contentLength
import io.ktor.http.isSuccess
import io.ktor.util.cio.writeChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.io.copyAndClose
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import java.io.File
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

            if (downloadStatus == DownloadManager.STATUS_SUCCESSFUL) {
                shareFile(getPath(mContext,Uri.parse(downloadLocalUri)))
            }
        }
        close()
    }
}

fun Context.shareFile(filePath: String?) {

    filePath?.let { path ->
        val file = File(path)
        if (!file.exists())
            return

        val uri = FileProvider.getUriForFile(this, "${BuildConfig.APPLICATION_ID}.provider", file)
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = uri.getMimeTypeForIntent()
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, SUBJECT)
            putExtra(Intent.EXTRA_TEXT, BODY)
        }
        startActivity(Intent.createChooser(shareIntent, "Share using"))
    }
}


fun Uri.getMimeTypeForIntent() = this.toString().run {
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

fun getMimeType(file: File): String? {
    val extension = file.extension
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
}