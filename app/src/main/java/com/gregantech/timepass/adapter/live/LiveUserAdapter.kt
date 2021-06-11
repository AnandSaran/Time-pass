package com.gregantech.timepass.adapter.live

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gregantech.timepass.databinding.ItemUserLiveBinding

class LiveUserAdapter(val callback: () -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        LiveUserViewHolder(
            ItemUserLiveBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as LiveUserViewHolder).bind()
    }

    override fun getItemCount() = 10

    inner class LiveUserViewHolder(private var itemUserLiveBinding: ItemUserLiveBinding) :
        RecyclerView.ViewHolder(itemUserLiveBinding.root) {

        fun bind(){
            with(itemUserLiveBinding){
                tvUserName.text = String.format("User %d", adapterPosition)
                rootLayout.setOnClickListener {
                    callback.invoke()
                }
            }
        }

    }

}