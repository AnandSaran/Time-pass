package com.gregantech.timepass.viewholder.rail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gregantech.timepass.adapter.handler.rail.RailItemClickHandler
import com.gregantech.timepass.databinding.ItemRailTypeThreeBinding
import com.gregantech.timepass.model.RailItemTypeThreeModel
import com.gregantech.timepass.util.extension.loadUrl
import com.gregantech.timepass.util.extension.visible

/**
 * Created by anand
 */
class RailItemTypeThreeViewHolder(
    var railBinding: ItemRailTypeThreeBinding
) :
    RecyclerView.ViewHolder(railBinding.root) {
    fun bind(
        railItem: RailItemTypeThreeModel,
        railItemClickHandler: RailItemClickHandler
    ) {
        itemView.tag = this
        railBinding.apply {
            val poster = if (railItem.isImage) {
                railItem.image
            } else {
                railItem.videoImage
            }

            ivPlay.visible(!railItem.isImage)
            ivPoster.loadUrl(poster)
            ivPoster.setOnClickListener {
                onClickPoster(railItemClickHandler, railItem)
            }

            executePendingBindings()
        }
    }

    private fun onClickPoster(
        railItemClickHandler: RailItemClickHandler,
        railItem: RailItemTypeThreeModel
    ) {
        if (railItemClickHandler.isInitializedForPosterClicking()) {
            railItemClickHandler.clickPoster(railItem)
        }
    }

    companion object {
        fun from(
            parent: ViewGroup
        ): RailItemTypeThreeViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemRailTypeThreeBinding.inflate(layoutInflater, parent, false)
            return RailItemTypeThreeViewHolder(binding)
        }
    }
}