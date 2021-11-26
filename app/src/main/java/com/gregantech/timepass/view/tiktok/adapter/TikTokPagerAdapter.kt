package com.gregantech.timepass.view.tiktok.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.gregantech.timepass.network.response.Video
import com.gregantech.timepass.view.tiktok.fragment.TikTokFragment

class TikTokPagerAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {

    var dataList: MutableList<Video> = mutableListOf()

    fun refresh(newDataList: List<Video>) {
        val oldPosition = dataList.size
        val newPosition = dataList.size + newDataList.size
        dataList.addAll(newDataList)
        notifyItemRangeInserted(oldPosition, newPosition)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun createFragment(position: Int): Fragment {
        return TikTokFragment.newInstance(dataList[position])
    }
}