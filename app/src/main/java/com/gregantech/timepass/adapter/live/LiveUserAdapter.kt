package com.gregantech.timepass.adapter.live

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gregantech.timepass.R
import com.gregantech.timepass.databinding.ItemUserLiveBinding
import com.gregantech.timepass.model.ListItem
import com.gregantech.timepass.util.extension.loadUrlCircle

class LiveUserAdapter(val callback: (ListItem) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var userList: List<ListItem?>? = null

    fun refresh(newList: List<ListItem?>?) {
        userList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        LiveUserViewHolder(
            ItemUserLiveBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as LiveUserViewHolder).bind(userList?.get(position))
    }

    override fun getItemCount() = userList?.size ?: 0

    inner class LiveUserViewHolder(private var itemUserLiveBinding: ItemUserLiveBinding) :
        RecyclerView.ViewHolder(itemUserLiveBinding.root) {

        fun bind(item: ListItem?) {
            with(itemUserLiveBinding) {
                tvUserName.text = item?.userName
                ivUserPic.loadUrlCircle(item?.profileImage, R.drawable.place_holder_profile)
                rootLayout.setOnClickListener {
                    callback.invoke(item!!)
                }
            }
        }

    }

}