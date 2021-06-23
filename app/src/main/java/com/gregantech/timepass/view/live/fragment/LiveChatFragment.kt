package com.gregantech.timepass.view.live.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gregantech.timepass.R
import com.gregantech.timepass.adapter.live.LiveChatAdapter
import com.gregantech.timepass.base.TimePassBaseFragment
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.databinding.FragmentLiveChatBinding
import com.gregantech.timepass.firestore.FireStoreConst
import com.gregantech.timepass.firestore.REACTION
import com.gregantech.timepass.model.ChatModel
import com.gregantech.timepass.network.repository.FireStoreRepository
import com.gregantech.timepass.util.constant.VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
import com.gregantech.timepass.util.extension.hide
import com.gregantech.timepass.util.extension.show
import com.gregantech.timepass.util.extension.toBitmap
import com.gregantech.timepass.util.extension.toast
import com.gregantech.timepass.util.sharedpreference.SharedPreferenceHelper
import com.gregantech.timepass.view.live.viewmodel.LiveChatViewModel
import com.gregantech.timepass.widget.heart.HeartsView
import kotlinx.android.synthetic.main.fragment_live_chat.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LiveChatFragment : TimePassBaseFragment() {

    private var docKey: String? = null
    private var mode = 0
    private var isScrolling = false

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
        subscribeToObservers()
        getChatList()
    }

    private fun initVMF() {
        viewModelFactory = LiveChatViewModel.Factory(FireStoreRepository())
    }

    private fun initView() {
        binding.ivLove.apply {
            if (mode == 1) hide() else show()
        }
        binding.fabSend.isEnabled = false
        binding.rvChat.apply {
            adapter = LiveChatAdapter()
            (layoutManager as LinearLayoutManager).stackFromEnd = true
            addOnScrollListener(scrollListener)
        }
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            (recyclerView.layoutManager as LinearLayoutManager?)?.run {
                val firstVisibleProductPosition = findFirstVisibleItemPosition()
                val visibleProductCount = childCount
                val totalProductCount = itemCount
                if (isScrolling && firstVisibleProductPosition + visibleProductCount == totalProductCount) {
                    isScrolling = false
                    getChatList()
                }
            }
        }
    }

    private fun setupOnClick() {
        with(binding) {
            etComment.setOnEditorActionListener { textView, i, keyEvent ->
                heartsView.emitHeart(obtainHeartModel())
                doSendMessage()
                false
            }
            etComment.addTextChangedListener(eventListener)
            fabSend.setOnClickListener {
                doSendMessage()
            }
            ivLove.setOnClickListener {
                ivLove.playAnimation()
                doIncreaseLoveCount()
            }
        }

    }

    private fun checkHearEmit() {
        viewLifecycleOwner.lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                kotlinx.coroutines.delay(500)
                withContext(Dispatchers.Main) {
                    heartsView.emitHeart(obtainHeartModel())
                    checkHearEmit()
                }
            }
        }
    }

    private fun subscribeToObservers() {

        //checkHearEmit()

        viewModel.obReactionCount(docKey!!).observe(viewLifecycleOwner, Observer {
            when (it.status) {
                TimePassBaseResult.Status.SUCCESS -> {
                    if (it?.data?.loves?.toInt() ?: 0 > 0) {
                        binding.heartsView.emitHeart(obtainHeartModel())
                    }
                }
                TimePassBaseResult.Status.ERROR -> Log.e(
                    "LiveBroadCast",
                    "listenToIncomingMsg: ${it.message}"
                )
                TimePassBaseResult.Status.LOADING -> {
                }
            }
        })

    }

    private fun obtainHeartModel() = HeartsView.Model(
        (0..10000).random(),
        ContextCompat.getDrawable(requireContext(), R.drawable.ic_heart)?.toBitmap()!!
    )

    private fun doIncreaseLoveCount() {
        viewModel.obUpdateReactionCount(docKey!!, REACTION.LOVE).observe(
            viewLifecycleOwner,
            Observer {
                when (it.status) {
                    TimePassBaseResult.Status.SUCCESS -> Log.d(
                        TAG,
                        "doIncreaseLoveCount: Count Updated!"
                    )
                    TimePassBaseResult.Status.ERROR -> Log.d(
                        TAG,
                        "doIncreaseLoveCount: Error updating count ${it.message}"
                    )
                    TimePassBaseResult.Status.LOADING -> {
                    }
                }
            })
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

    private fun getChatList() {
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