package com.gregantech.timepass.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 *  model for Rail item type 1 inside rail
 */
@Parcelize
class RailItemTypeOneModel(
    override val contentId: String,
    override val title: String = "",
    override val image: String = "",
    override val railItemType: RailItemTypeEnum = RailItemTypeEnum.TYPE_RAIL_ITEM_ONE
) : Parcelable, RailBaseItemModel()