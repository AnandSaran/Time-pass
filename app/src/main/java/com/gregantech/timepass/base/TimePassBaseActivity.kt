package com.gregantech.timepass.base

import androidx.appcompat.app.AppCompatActivity
import com.gregantech.timepass.model.DownloadResult
import com.gregantech.timepass.model.RailItemTypeTwoModel
import com.gregantech.timepass.util.extension.downloadFile
import com.gregantech.timepass.util.extension.shareVideoText
import com.gregantech.timepass.widget.CustomProgressbar
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

/**
 * Created by anand on 2020-11-06.
 */

abstract class TimePassBaseActivity : AppCompatActivity() {
    private lateinit var customProgressbar: CustomProgressbar
    protected val TAG = this.javaClass.simpleName
    val ktor: HttpClient by inject()

    fun showProgressBar() {
        getProgressBar().show()
    }

    fun dismissProgressBar() {
        runOnUiThread {
            try {
                getProgressBar().dismissProgress()
            } catch (e: Exception) {

            }
        }
    }

    private fun getProgressBar(): CustomProgressbar {
        if (!::customProgressbar.isInitialized) {
            customProgressbar = CustomProgressbar(this)
        }
        return customProgressbar
    }

    fun downloadWithFlow(model: RailItemTypeTwoModel) {
        CoroutineScope(Dispatchers.IO).launch {
            ktor.downloadFile(model.file, model.video).collect {
                withContext(Dispatchers.Main) {
                    when (it) {
                        is DownloadResult.Success -> {
                            shareVideoText(model.file)
                        }
                        is DownloadResult.Error -> {
                        }
                        is DownloadResult.Progress -> {
                        }
                    }
                }
            }
        }
    }
}