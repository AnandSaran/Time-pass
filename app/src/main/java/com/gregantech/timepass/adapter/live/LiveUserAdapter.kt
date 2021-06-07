package com.gregantech.timepass.adapter.live

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gregantech.timepass.viewholder.live.LiveUserViewHolder

class LiveUserAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = LiveUserViewHolder.from(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as LiveUserViewHolder).bind()
    }

    override fun getItemCount() = 10
}