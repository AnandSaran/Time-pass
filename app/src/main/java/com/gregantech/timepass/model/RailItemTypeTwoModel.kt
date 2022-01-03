package com.gregantech.timepass.model

import android.net.Uri
import android.os.Environment
import android.os.Parcelable
import androidx.core.net.toUri
import com.gregantech.timepass.util.constant.EMPTY_STRING
import com.gregantech.timepass.util.extension.globalContext
import com.gregantech.timepass.util.extension.stripFileNameFromUrl
import kotlinx.android.parcel.Parcelize
import java.io.File

/**
 *  model for Rail item type 1 inside rail
 */
@Parcelize
class RailItemTypeTwoModel(
    override val contentId: String="",
    override val title: String = "",
    val subtitle: String = "",
    override val image: String = "",
    override val railItemType: RailItemTypeEnum = RailItemTypeEnum.TYPE_RAIL_ITEM_TWO,
    val video: String = "",
   /* val width: String = "0.0",
    val height: String = "0.0",*/
    var isLiked: Boolean = false,
    var isFollowed: Boolean = false,
    var followerId: String = "",
    var totalLike: Int = 0,
    var totalComment: Int = 0,
    var isShowFollow: Boolean = true,
    var isShowProfile: Boolean = true,
    var isImage: Boolean? = false,
    var userName: String = EMPTY_STRING,
    var userImage: String = EMPTY_STRING,
    var viewType: Int = 0,
    var position: Int = 0,
    var timeStamp: String? = null
) : Parcelable, RailBaseItemModel(){
    val uriFile: Uri
        get() = file.toUri()
    val file: File
        get() = File(globalContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), title)
}

fun RailItemTypeTwoModel.getFileToDownload() = if(video.isEmpty()) image else video

fun RailItemTypeTwoModel.getStrippedFileName() = (if(video.isEmpty()) image else video).stripFileNameFromUrl()