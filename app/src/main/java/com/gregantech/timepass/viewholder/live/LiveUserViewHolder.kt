package com.gregantech.timepass.viewholder.live

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gregantech.timepass.databinding.ItemUserLiveBinding

class LiveUserViewHolder(private var itemUserLiveBinding: ItemUserLiveBinding) :
    RecyclerView.ViewHolder(itemUserLiveBinding.root) {

    fun bind(){
        itemUserLiveBinding.apply {
            tvUserName.text = String.format("User %d", adapterPosition)
        }
    }

    companion object {
        fun from(parent: ViewGroup): LiveUserViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemUserLiveBinding.inflate(layoutInflater, parent, false)
            return LiveUserViewHolder(binding)
        }
    }

}