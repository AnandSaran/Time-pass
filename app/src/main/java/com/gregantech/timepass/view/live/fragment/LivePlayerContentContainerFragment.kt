package com.gregantech.timepass.view.live.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import com.gregantech.timepass.R
import com.gregantech.timepass.base.TimePassBaseFragment
import com.gregantech.timepass.databinding.FragmentLivePlayerContentContainerBinding
import com.gregantech.timepass.util.navigation.FragmentNavigationUtil

class LivePlayerContentContainerFragment : TimePassBaseFragment() {


    private lateinit var mContext: Context
    private lateinit var binding: FragmentLivePlayerContentContainerBinding

    companion object {
        fun newInstance() = LivePlayerContentContainerFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_live_player_content_container,
                container,
                false
            )
        context?.let {
            mContext = it
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showLivePlayerFragment()
        showChatFragment()
    }

    private fun showLivePlayerFragment() {
        val playerFragment = LivePlayerFragment.newInstance()

        commitFragment(playerFragment, childFragmentManager, R.id.playerContainer)
    }

    private fun showChatFragment() {
        /* val overlayFragment = ChatFragment.newInstance()

         commitFragment(
             overlayFragment,
             childFragmentManager,
             R.id.playerLandscapeControllerContainer
         )*/
    }

    private fun commitFragment(
        fragment: TimePassBaseFragment,
        fragmentManager: FragmentManager,
        container: Int
    ) {
        FragmentNavigationUtil.commitFragment(
            fragment,
            fragmentManager,
            container
        )
    }
}