package com.gregantech.timepass.view.live.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.gregantech.timepass.R
import com.gregantech.timepass.adapter.live.LiveChatAdapter
import com.gregantech.timepass.base.TimePassBaseFragment
import com.gregantech.timepass.databinding.FragmentLiveChatBinding

class LiveChatFragment : TimePassBaseFragment() {

    private lateinit var binding: FragmentLiveChatBinding

    companion object {
        @JvmStatic
        fun newInstance() =
            LiveChatFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!::binding.isInitialized) {
            binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_live_chat,
                container,
                false
            )
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.rvChat.adapter = LiveChatAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupOnClick()
    }

    private fun setupOnClick() {

    }


}