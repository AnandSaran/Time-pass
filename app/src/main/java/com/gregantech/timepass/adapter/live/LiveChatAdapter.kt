package com.gregantech.timepass.adapter.live

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gregantech.timepass.databinding.ItemLiveChatBinding

class LiveChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        LiveChatViewHolder(
            ItemLiveChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as LiveChatViewHolder).bind()
    }

    override fun getItemCount() = 15

    inner class LiveChatViewHolder(private var itemLiveChatBinding: ItemLiveChatBinding) :
        RecyclerView.ViewHolder(itemLiveChatBinding.root) {

        fun bind() {
            with(itemLiveChatBinding) {
                tvMsg.text = String.format("User %d", adapterPosition)
            }
        }

    }

}