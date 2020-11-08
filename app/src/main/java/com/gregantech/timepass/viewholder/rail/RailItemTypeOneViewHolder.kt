package com.gregantech.timepass.viewholder.rail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gregantech.timepass.adapter.handler.rail.RailItemClickHandler
import com.gregantech.timepass.databinding.ItemRailTypeOneBinding
import com.gregantech.timepass.model.RailItemTypeOneModel
import com.gregantech.timepass.util.extension.loadUrl

/**
 * Created by anand
 */
class RailItemTypeOneViewHolder(private var railBinding: ItemRailTypeOneBinding) :
    RecyclerView.ViewHolder(railBinding.root) {

    fun bind(
        railItem: RailItemTypeOneModel,
        railItemClickHandler: RailItemClickHandler
    ) {
        railBinding.apply {
            tvTitle.text = railItem.title
            imageViewPhoto.loadUrl(railItem.image)
            cardViewLayout.setOnClickListener {
                if (railItemClickHandler.isInitializedForPosterClicking()) {
                    railItemClickHandler.clickPoster(railItem)
                }
            }
            executePendingBindings()
        }
    }

    companion object {
        fun from(parent: ViewGroup): RailItemTypeOneViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemRailTypeOneBinding.inflate(layoutInflater, parent, false)
            return RailItemTypeOneViewHolder(binding)
        }
    }
}