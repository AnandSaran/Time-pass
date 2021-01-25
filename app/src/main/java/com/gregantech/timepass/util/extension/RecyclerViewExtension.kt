package com.gregantech.timepass.util.extension

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.gregantech.timepass.R
import com.gregantech.timepass.model.RailItemDecorationTypeEnum
import com.gregantech.timepass.util.itemdecoration.CategoryGridItemDecoration

fun RecyclerView.setCustomDivider(drawable: Int) {
    val divider = ContextCompat.getDrawable(this.context, drawable)
    divider?.let {
        val itemDecoration = DividerItemDecoration(
            this.context,
            DividerItemDecoration.VERTICAL
        )
        itemDecoration.setDrawable(it)
        addItemDecoration(itemDecoration)
    }
}

fun RecyclerView.generateRailItemDecoration(
    railItemDecorationType: RailItemDecorationTypeEnum
) {
    val itemDecoration: RecyclerView.ItemDecoration
    when (railItemDecorationType) {
        RailItemDecorationTypeEnum.TYPE_RAIL_ITEM_DECORATION_ONE -> {
            itemDecoration = CategoryGridItemDecoration(
                spacingHorizontal = resources.getDimensionPixelOffset(R.dimen.item_category_grid_spacing_horizontal),
                spacingVertical = resources.getDimensionPixelOffset(R.dimen.item_category_grid_spacing_vertical),
                spanCount = resources.getInteger(R.integer.recycler_view_category_span_count),
                includeEdge = true
            )
            addItemDecoration(itemDecoration)
        }
        RailItemDecorationTypeEnum.TYPE_RAIL_ITEM_DECORATION_TWO -> {
           /* itemDecoration = CategoryDetailItemDecoratoration(
                resources.getDimensionPixelOffset(R.dimen.item_category_video_spacing_vertical)
            )
            addItemDecoration(itemDecoration)*/
        }
        else -> {
        }
    }
}