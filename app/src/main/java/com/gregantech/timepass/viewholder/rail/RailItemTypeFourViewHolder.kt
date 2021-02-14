package com.gregantech.timepass.viewholder.rail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gregantech.timepass.R
import com.gregantech.timepass.adapter.handler.rail.RailItemClickHandler
import com.gregantech.timepass.databinding.ItemRailTypeFourBinding
import com.gregantech.timepass.model.RailItemTypeFourModel
import com.gregantech.timepass.util.extension.loadUrlCircle

/**
 * Created by anand
 */
class RailItemTypeFourViewHolder(
    var railBinding: ItemRailTypeFourBinding
) :
    RecyclerView.ViewHolder(railBinding.root) {
    fun bind(
        railItem: RailItemTypeFourModel,
        railItemClickHandler: RailItemClickHandler
    ) {
        itemView.tag = this
        railBinding.apply {
            tvUserName.text = railItem.title
            ivUserProfile.loadUrlCircle(
                railItem.image, R.drawable.place_holder_profile
            )
            cardViewLayout.setOnClickListener {
                onClickPoster(railItemClickHandler, railItem)
            }
            executePendingBindings()
        }
    }

    private fun onClickPoster(
        railItemClickHandler: RailItemClickHandler,
        railItem: RailItemTypeFourModel
    ) {
        if (railItemClickHandler.isInitializedForPosterClicking()) {
            railItemClickHandler.clickPoster(railItem)
        }
    }

    companion object {
        fun from(
            parent: ViewGroup
        ): RailItemTypeFourViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemRailTypeFourBinding.inflate(layoutInflater, parent, false)
            return RailItemTypeFourViewHolder(binding)
        }
    }
}