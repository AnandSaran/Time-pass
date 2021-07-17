package com.gregantech.timepass.view.topic.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gregantech.timepass.R
import com.gregantech.timepass.base.TimePassBaseFragment
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.databinding.FragmentTopicBinding
import com.gregantech.timepass.network.repository.TopicRepository
import com.gregantech.timepass.util.Run
import com.gregantech.timepass.util.constant.VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
import com.gregantech.timepass.util.navigation.FragmentNavigationUtil
import com.gregantech.timepass.view.topic.model.TopicResponse
import com.gregantech.timepass.view.topic.viewmodel.TopicViewModel


class TopicFragment : TimePassBaseFragment() {

    private lateinit var binding: FragmentTopicBinding
    private var topicItem: TopicResponse.TopicItem? = null
    private lateinit var topicViewModelFactory: TopicViewModel.Factory

    private val topicViewModel: TopicViewModel by lazy {
        requireNotNull(this) {
            VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
        }
        ViewModelProvider(this, topicViewModelFactory).get(TopicViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_topic, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModelFactory()
        subscribeToChanges()
    }

    private fun subscribeToChanges() {
        topicViewModel.getAppConfig().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                TimePassBaseResult.Status.SUCCESS -> {
                    topicItem = it.data?.topic?.get(0)
                    bindTopicToView()
                    Run.after(500) {
                        binding.vsTopic.setChildVisible()
                    }
                }
                TimePassBaseResult.Status.ERROR -> {
                }
                TimePassBaseResult.Status.LOADING -> {
                }
            }
        })
    }

    private fun bindTopicToView() {
        topicItem?.run {
            binding.tvTopicName.text = topicName
            loadChatContainerFragment()
        }
    }

    private fun setupViewModelFactory() {
        topicViewModelFactory = TopicViewModel.Factory(TopicRepository())
    }


    private fun loadChatContainerFragment() {

        Log.d(TAG, "loadChatContainerFragment: " + topicItem?.Id)

        if (topicItem?.Id.isNullOrEmpty())
            return
        val chatFragment = TopicChatFragment.newInstance(docKey = topicItem?.Id)
        FragmentNavigationUtil.commitFragment(
            chatFragment,
            childFragmentManager,
            R.id.topicChatContainer
        )
    }

}