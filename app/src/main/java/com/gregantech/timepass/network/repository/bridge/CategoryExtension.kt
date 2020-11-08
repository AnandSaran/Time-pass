package com.gregantech.timepass.network.repository.bridge

import com.gregantech.timepass.model.RailBaseItemModel
import com.gregantech.timepass.model.RailItemTypeOneModel
import com.gregantech.timepass.network.response.Category

fun List<Category>.toRailItemTypeOneModelList(): ArrayList<RailBaseItemModel> {
    return ArrayList(this.map { category ->
        RailItemTypeOneModel(
            contentId = category.catID,
            title = category.catName,
            image = category.catImage
        )
    })
}