package com.gregantech.timepass.view.live.fragment

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
import com.gregantech.timepass.adapter.live.LiveChatAdapter
import com.gregantech.timepass.base.TimePassBaseFragment
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.databinding.FragmentLiveChatBinding
import com.gregantech.timepass.firestore.FireStoreConst
import com.gregantech.timepass.model.ChatModel
import com.gregantech.timepass.network.repository.LiveChatRepository
import com.gregantech.timepass.util.constant.VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
import com.gregantech.timepass.util.extension.gone
import com.gregantech.timepass.util.extension.show
import com.gregantech.timepass.util.extension.toast
import com.gregantech.timepass.util.sharedpreference.SharedPreferenceHelper
import com.gregantech.timepass.view.live.viewmodel.LiveChatViewModel

class LiveChatFragment : TimePassBaseFragment() {

    private var docKey: String? = null
    private var mode = 0

    private lateinit var binding: FragmentLiveChatBinding
    private lateinit var viewModelFactory: LiveChatViewModel.Factory

    private val viewModel: LiveChatViewModel by lazy {
        requireNotNull(this.activity) {
            VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
        }
        ViewModelProvider(this, viewModelFactory).get(LiveChatViewModel::class.java)
    }


    companion object {
        @JvmStatic
        fun newInstance(mode: Int = 0, docKey: String? = null) = LiveChatFragment().apply {
            arguments = Bundle().apply {
                putString(FireStoreConst.Keys.DOC_KEY, docKey)
                putInt(FireStoreConst.Keys.MODE, mode)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.run {
            docKey = getString(FireStoreConst.Keys.DOC_KEY)
            mode = getInt(FireStoreConst.Keys.MODE, 0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!::binding.isInitialized) {
            binding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_live_chat, container, false)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initVMF()
        initView()
        setupOnClick()
        listenToIncomingMsg()
    }

    private fun initVMF() {
        viewModelFactory = LiveChatViewModel.Factory(LiveChatRepository())
    }

    private fun initView() {
        binding.bottomLay.apply {
            if (mode == 1) gone() else show()
        }
        binding.fabSend.isEnabled = false
        binding.rvChat.apply {
            adapter = LiveChatAdapter()
            (layoutManager as LinearLayoutManager).stackFromEnd = true
        }
    }

    private fun setupOnClick() {
        binding.etComment.setOnEditorActionListener { textView, i, keyEvent ->
            doSendMessage()
            false
        }
        binding.fabSend.setOnClickListener {
            doSendMessage()
        }
        binding.etComment.addTextChangedListener(eventListener)
    }

    private val eventListener = object : TextWatcher {

        override fun beforeTextChanged(str: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(str: CharSequence?, p1: Int, p2: Int, p3: Int) {
            binding.fabSend.isEnabled = str?.length ?: 0 >= 1
        }

        override fun afterTextChanged(str: Editable?) {

        }

    }

    private fun doSendMessage() {

        if (binding.etComment.text.toString().isEmpty())
            return

        viewModel.obOutgoingMessage(obtainChatModel(), docKey!!)
            .observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    TimePassBaseResult.Status.LOADING -> {
                    }
                    TimePassBaseResult.Status.SUCCESS -> {
                        Log.d(TAG, "doSendMessage: DocId ${it.data.toString()}")
                    }
                    TimePassBaseResult.Status.ERROR -> {
                        it.message?.toast(requireContext())
                    }
                }
            })
        binding.etComment.setText("")
        binding.fabSend.isEnabled = false
    }

    private fun obtainChatModel() = ChatModel(
        commentedUserId = SharedPreferenceHelper.getUserId(),
        commentedUserProfileUrl = SharedPreferenceHelper.getUserProfileImage(),
        commentedUserName = SharedPreferenceHelper.getUserName(),
        comments = binding.etComment.text.toString()
    )

    override fun onDestroyView() {
        binding.etComment.removeTextChangedListener(eventListener)
        super.onDestroyView()
    }

    private fun listenToIncomingMsg() {
        viewModel.obIncomingMessage(docKey!!)?.observe(viewLifecycleOwner, Observer {
            with(binding.rvChat.adapter as LiveChatAdapter) {
                when (it.type) {
                    R.string.added -> addProduct(it.chatModel)
                    R.string.modified -> modifyProduct(it.chatModel)
                    R.string.removed -> removeProduct(it.chatModel)
                }
                binding.rvChat.smoothScrollToPosition(if (itemCount > 0) itemCount - 1 else 0)
            }
        })

    }

    fun onBackPressed() {
        findNavController().popBackStack()
    }


}