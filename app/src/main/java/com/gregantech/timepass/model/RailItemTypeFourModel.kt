package com.gregantech.timepass.model

import android.os.Parcelable
import com.gregantech.timepass.util.constant.EMPTY_STRING
import kotlinx.android.parcel.Parcelize

/**
 *  model for Rail item type 4 inside rail
 */
@Parcelize
class RailItemTypeFourModel(
    override val contentId: String,
    override val title: String = EMPTY_STRING,
    override val image: String = EMPTY_STRING,
    override val railItemType: RailItemTypeEnum = RailItemTypeEnum.TYPE_RAIL_ITEM_FOUR
) : Parcelable, RailBaseItemModel()