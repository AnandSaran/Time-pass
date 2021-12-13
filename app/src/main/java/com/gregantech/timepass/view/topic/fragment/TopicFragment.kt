package com.gregantech.timepass.view.topic.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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
        topicViewModel.getTopics().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                TimePassBaseResult.Status.SUCCESS -> {

                    if (it.data?.topic?.isNullOrEmpty() == true) {
                        with(binding) {
                            vsTopic.setChildVisible()
                            tvMessage.text = getString(R.string.no_topis_available)
                            vsTopicContent.setChildVisible()
                        }
                    } else {
                        topicItem = it.data?.topic?.get(0)
                        bindTopicToView()
                        Run.after(500) {
                            with(binding) {
                                vsTopic.setChildVisible()
                                vsTopicContent.setParentVisible()
                            }
                        }
                    }
                }
                TimePassBaseResult.Status.ERROR -> with(binding) {
                    vsTopic.setChildVisible()
                    tvMessage.apply {
                        text = it.message
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.heartRed))
                    }
                    vsTopicContent.setChildVisible()
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