package com.gregantech.timepass.view.tiktok.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.gregantech.timepass.network.response.Video
import com.gregantech.timepass.view.tiktok.fragment.TikTokAdFragment
import com.gregantech.timepass.view.tiktok.fragment.TikTokFragment

class TikTokPagerAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {

    var dataList: MutableList<Video> = mutableListOf()
    var playPosition = 0L
    lateinit var tikTokFragment: TikTokFragment

    fun refresh(newDataList: ArrayList<Video>) {
        val oldPosition = dataList.size
        val newPosition = dataList.size + newDataList.size
        dataList.addAll(newDataList)
        notifyItemRangeInserted(oldPosition, newPosition)
    }

    override fun getItemCount() = dataList.size

    override fun createFragment(position: Int): Fragment {
        if (position != 0 && dataList[position].viewType == 1) {
            return TikTokAdFragment()
        }
        tikTokFragment = TikTokFragment.newInstance(dataList[position], playPosition)
        return tikTokFragment
    }
}