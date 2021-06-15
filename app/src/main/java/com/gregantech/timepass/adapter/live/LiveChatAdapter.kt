package com.gregantech.timepass.adapter.live

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gregantech.timepass.R
import com.gregantech.timepass.databinding.ItemLiveChatBinding
import com.gregantech.timepass.model.ChatModel
import com.gregantech.timepass.util.extension.loadUrlCircle

class LiveChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val chatList = ArrayList<ChatModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        LiveChatViewHolder(
            ItemLiveChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as LiveChatViewHolder).bind(chatList[position])
    }

    override fun getItemCount() = chatList.size

    inner class LiveChatViewHolder(private var itemLiveChatBinding: ItemLiveChatBinding) :
        RecyclerView.ViewHolder(itemLiveChatBinding.root) {

        fun bind(chatModel: ChatModel) {
            with(itemLiveChatBinding) {
                ivUserPic.loadUrlCircle(
                    chatModel.commentedUserProfileUrl ?: "",
                    R.drawable.place_holder_profile
                )
                tvUserName.text = chatModel.commentedUserName
                tvComment.text = chatModel.comments
            }
        }
    }

    fun addProduct(addedProduct: ChatModel) {
        chatList.add(addedProduct)
        notifyItemInserted(chatList.size - 1)
    }

    fun modifyProduct(modifiedProduct: ChatModel) {
        for (index in 0 until chatList.size) {
            if (chatList[index].id == modifiedProduct.id) {
                chatList[index] = modifiedProduct
                notifyItemChanged(index)
                break
            }
        }
    }

    fun removeProduct(removedProduct: ChatModel) {
        for (index in 0 until chatList.size) {
            if (chatList[index].id == removedProduct.id) {
                chatList.removeAt(index)
                notifyItemRemoved(index)
                break
            }
        }
    }

}