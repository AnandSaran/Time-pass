package com.gregantech.timepass.view.topic.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gregantech.timepass.R
import com.gregantech.timepass.base.TimePassBaseFragment
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.databinding.FragmentTopicChatBinding
import com.gregantech.timepass.firestore.FireStoreConst
import com.gregantech.timepass.model.ChatModel
import com.gregantech.timepass.network.repository.FireStoreRepository
import com.gregantech.timepass.util.constant.VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
import com.gregantech.timepass.util.extension.toast
import com.gregantech.timepass.util.sharedpreference.SharedPreferenceHelper
import com.gregantech.timepass.view.live.viewmodel.LiveChatViewModel
import com.gregantech.timepass.view.topic.adapter.TopicChatAdapter
import com.gregantech.timepass.widget.PaginationScrollListener


class TopicChatFragment : TimePassBaseFragment() {

    private lateinit var binding: FragmentTopicChatBinding
    private lateinit var viewModelFactory: LiveChatViewModel.Factory
    private var isLoading = false
    private var topicChatAdapter: TopicChatAdapter? = null
    private var docKey: String? = null
    private var totalChatCount: Long = 0

    private val viewModel: LiveChatViewModel by lazy {
        requireNotNull(this.activity) {
            VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
        }
        ViewModelProvider(this, viewModelFactory).get(LiveChatViewModel::class.java)
    }


    companion object {
        @JvmStatic
        fun newInstance(docKey: String? = null) = TopicChatFragment().apply {
            arguments = Bundle().apply {
                putString(FireStoreConst.Keys.DOC_KEY, docKey)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.run {
            docKey = getString(FireStoreConst.Keys.DOC_KEY)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_topic_chat, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initVMF()
        initView()
        setupOnClick()
        subscribeToObservers()
        getChatList()
    }

    private fun initVMF() {
        viewModelFactory = LiveChatViewModel.Factory(FireStoreRepository())
    }

    private fun initView() {
        topicChatAdapter = TopicChatAdapter()
        binding.rvTopicChat.apply {
            adapter = topicChatAdapter
            (layoutManager as LinearLayoutManager).stackFromEnd = true
        }
    }

    override fun onResume() {
        super.onResume()
        if (isAdded) {
            binding.rvTopicChat.addOnScrollListener(object :
                PaginationScrollListener(binding.rvTopicChat.layoutManager as LinearLayoutManager) {
                override fun isLastPage(): Boolean {
                    val loadedCount = topicChatAdapter?.itemCount
                    return loadedCount == totalChatCount.toInt()
                }

                override fun isLoading(): Boolean {
                    return isLoading
                }

                override fun onItemIsFirstVisibleItem(index: Int) {

                }

                override fun loadMoreItems() {
                    isLoading = true
                    getChatList()
                }
            })
        }
    }


    private fun setupOnClick() {
        with(binding) {
            edtComments.setOnEditorActionListener { textView, i, keyEvent ->
                doSendMessage()
                false
            }
            edtComments.addTextChangedListener(eventListener)
            btnComment.setOnClickListener {
                doSendMessage()
            }
        }

    }

    private fun subscribeToObservers() {


    }

    private val eventListener = object : TextWatcher {

        override fun beforeTextChanged(str: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(str: CharSequence?, p1: Int, p2: Int, p3: Int) {
            binding.btnComment.isEnabled = str?.length ?: 0 >= 1
        }

        override fun afterTextChanged(str: Editable?) {

        }

    }

    private fun doSendMessage() {

        if (binding.edtComments.text.toString().isEmpty())
            return

        val chatModel = obtainChatModel()
        viewModel.obOutgoingTopicMessage(chatModel, docKey!!)
            .observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    TimePassBaseResult.Status.LOADING -> {
                    }
                    TimePassBaseResult.Status.SUCCESS -> {
                        Log.d("TopicChat", "doSendMessage: ")
                        totalChatCount += 1
                    }
                    TimePassBaseResult.Status.ERROR -> {
                        it.message?.toast(requireContext())
                    }
                }
            })

        binding.edtComments.setText("")
        binding.btnComment.isEnabled = false
    }

    private fun obtainChatModel() = ChatModel(
        commentedUserId = SharedPreferenceHelper.getUserId(),
        commentedUserProfileUrl = SharedPreferenceHelper.getUserProfileImage(),
        commentedUserName = SharedPreferenceHelper.getUserName(),
        comments = binding.edtComments.text.toString()
    )

    override fun onDestroyView() {
        binding.edtComments.removeTextChangedListener(eventListener)
        super.onDestroyView()
    }

    private fun getChatList() {

        viewModel.obTopicIncomingMessage(docKey!!)
            ?.observe(viewLifecycleOwner, Observer {
                with(binding.rvTopicChat.adapter as TopicChatAdapter) {
                    isLoading = false
                    when (it.type) {
                        R.string.added -> addProduct(it.chatModel)
                        R.string.modified -> modifyProduct(it.chatModel)
                        R.string.removed -> removeProduct(it.chatModel)
                    }
                    binding.rvTopicChat.smoothScrollToPosition(if (itemCount > 0) itemCount - 1 else 0)
                }

            })
    }

    fun onBackPressed() {
        findNavController().popBackStack()
    }


}