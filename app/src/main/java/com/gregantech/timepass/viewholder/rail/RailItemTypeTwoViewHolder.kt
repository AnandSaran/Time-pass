package com.gregantech.timepass.viewholder.rail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.gregantech.timepass.adapter.handler.rail.RailItemClickHandler
import com.gregantech.timepass.databinding.ItemRailTypeTwoBinding
import com.gregantech.timepass.model.RailItemTypeTwoModel
import com.gregantech.timepass.util.extension.loadUrl

/**
 * Created by anand
 */
class RailItemTypeTwoViewHolder(
    var railBinding: ItemRailTypeTwoBinding
) :
    RecyclerView.ViewHolder(railBinding.root) {
    lateinit var railItem: RailItemTypeTwoModel
    fun bind(
        railItem: RailItemTypeTwoModel,
        railItemClickHandler: RailItemClickHandler
    ) {
        itemView.tag = this
        this.railItem = railItem
        railBinding.apply {
            clMediaContainer.updateLayoutParams<ConstraintLayout.LayoutParams> {
                dimensionRatio = railItem.width.plus(":").plus(railItem.height)
            }
            tvTitle.text = railItem.title
            imageViewPhoto.loadUrl(railItem.image)
            clParent.setOnClickListener {
                if (railItemClickHandler.isInitializedForPosterClicking()) {
                    railItemClickHandler.clickPoster(railItem)
                }
            }
            executePendingBindings()
        }
    }

    companion object {
        fun from(
            parent: ViewGroup
        ): RailItemTypeTwoViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemRailTypeTwoBinding.inflate(layoutInflater, parent, false)
            return RailItemTypeTwoViewHolder(binding)
        }
    }
}