package com.gregantech.timepass.model

import android.os.Parcelable
import com.gregantech.timepass.util.constant.EMPTY_STRING
import kotlinx.android.parcel.Parcelize

/**
 *  model for Rail item type 3 inside rail
 */
@Parcelize
class RailItemTypeThreeModel(
    override val contentId: String = EMPTY_STRING,
    override val title: String = EMPTY_STRING,
    val subtitle: String = EMPTY_STRING,
    override val image: String = EMPTY_STRING,
    override val railItemType: RailItemTypeEnum = RailItemTypeEnum.TYPE_RAIL_ITEM_THREE,
    var videoImage: String = EMPTY_STRING,
    var isImage: Boolean = false
) : Parcelable, RailBaseItemModel()