package com.gregantech.timepass.view.comment.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gregantech.timepass.R
import com.gregantech.timepass.adapter.comment.CommentAdapter
import com.gregantech.timepass.base.TimePassBaseActivity
import com.gregantech.timepass.base.TimePassBaseResult
import com.gregantech.timepass.databinding.ActivityCommentBinding
import com.gregantech.timepass.general.bundklekey.CommentBundleKeyEnum
import com.gregantech.timepass.model.CommentBaseItemModel
import com.gregantech.timepass.model.RailItemTypeTwoModel
import com.gregantech.timepass.network.repository.CommentListRepository
import com.gregantech.timepass.network.repository.bridge.commentTypeOneModel
import com.gregantech.timepass.network.repository.bridge.toCommentItemTypeOneModelList
import com.gregantech.timepass.network.response.comments.Comment
import com.gregantech.timepass.util.constant.EMPTY_BOOLEAN
import com.gregantech.timepass.util.constant.EMPTY_STRING
import com.gregantech.timepass.util.constant.JUST_NOW
import com.gregantech.timepass.util.constant.VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
import com.gregantech.timepass.util.extension.toast
import com.gregantech.timepass.util.extension.visible
import com.gregantech.timepass.util.sharedpreference.SharedPreferenceHelper
import com.gregantech.timepass.view.comment.viewmodel.CommentFragmentViewModel


class CommentActivity : TimePassBaseActivity() {
    private lateinit var ctxt: Context
    private lateinit var binding: ActivityCommentBinding
    private lateinit var viewModelFactory: CommentFragmentViewModel.Factory

    private var commentList: ArrayList<CommentBaseItemModel> = arrayListOf()

    private var postId: String = EMPTY_STRING
    private var isAdminPost: Boolean = EMPTY_BOOLEAN
    private var isUserPost: Boolean = EMPTY_BOOLEAN

    private val viewModel: CommentFragmentViewModel by lazy {
        requireNotNull(this) {
            VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
        }
        ViewModelProvider(this, viewModelFactory).get(CommentFragmentViewModel::class.java)
    }

    companion object {
        fun present(
            context: Context,
            postId: String,
            isAdminPost: Boolean = false,
            isUserPost: Boolean = false
        ) {
            val intent = Intent(context, CommentActivity::class.java)
            intent.putExtra(CommentBundleKeyEnum.POST_ID.value, postId)
            intent.putExtra(CommentBundleKeyEnum.IS_ADMIN_POST.value, isAdminPost)
            intent.putExtra(CommentBundleKeyEnum.IS_USER_POST.value, isUserPost)
            context.startActivity(intent)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_comment
        )

        viewModelFactory =
            CommentFragmentViewModel.Factory(
                CommentListRepository(),
                SharedPreferenceHelper
            )
        intent?.apply {
            postId = getStringExtra(CommentBundleKeyEnum.POST_ID.value) ?: EMPTY_STRING
            isAdminPost = getBooleanExtra(CommentBundleKeyEnum.IS_ADMIN_POST.value, EMPTY_BOOLEAN)
            isUserPost = getBooleanExtra(CommentBundleKeyEnum.IS_USER_POST.value, EMPTY_BOOLEAN)
        }
        setupViewModelObserver()
        setupToolBar()
        setupOnClick()
    }

    private fun setupOnClick() {
        binding.btnComment.setOnClickListener {
            if (isValidComment()) {
                if (isAdminPost) {
                    viewModel.setAdminVideoComment(binding.edtComments.text.toString(), postId)
                        .observe(this,
                            Observer { })
                    addCommentToList()
                } else if (isUserPost) {
                    viewModel.setUserVideoComment(binding.edtComments.text.toString(), postId)
                        .observe(this,
                            Observer { })
                    addCommentToList()
                }
            }
        }
    }

    private fun addCommentToList() {
        val comment = Comment(
            JUST_NOW,
            EMPTY_STRING,
            SharedPreferenceHelper.getUserProfileImage(),
            binding.edtComments.text.toString(),
            SharedPreferenceHelper.getUserId(),
            SharedPreferenceHelper.getUserName()
        )
        val commentTypeOne = comment.commentTypeOneModel()
        commentList.add(commentTypeOne)
        binding.rvCommentList.adapter?.notifyItemInserted(commentList.size)
        scrollEnd()
        clearCommentTextView()
    }

    private fun clearCommentTextView() {
        binding.edtComments.text = null
    }

    private fun isValidComment(): Boolean =
        binding.edtComments.text.toString().isNotBlank()

    private fun setupToolBar() {
        setSupportActionBar(binding.tbProfile.toolbar)
        setToolbarTitle()
        setToolbarBackButton()
    }

    private fun setToolbarTitle() {
        supportActionBar?.title = getString(R.string.comments)
    }

    private fun setToolbarBackButton() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setupViewModelObserver() {
        if (isAdminPost) {
            viewModel.getAdminVideoCommentList(postId)
                .observe(this, Observer { categoryListResponse ->
                    when (categoryListResponse.status) {
                        TimePassBaseResult.Status.LOADING -> {
                        }
                        TimePassBaseResult.Status.SUCCESS -> {
                            hideLoadingText()
                            categoryListResponse.data?.let {
                                commentList.addAll(it.comments.toCommentItemTypeOneModelList())
                                setupRecyclerView()
                            }
                        }
                        TimePassBaseResult.Status.ERROR -> {
                            hideLoadingText()
                            categoryListResponse.message?.toast(ctxt)
                        }
                    }
                })

        } else if (isUserPost) {
            viewModel.getUserVideoCommentList(postId)
                .observe(this, Observer { categoryListResponse ->
                    when (categoryListResponse.status) {
                        TimePassBaseResult.Status.LOADING -> {
                        }
                        TimePassBaseResult.Status.SUCCESS -> {
                            hideLoadingText()
                            categoryListResponse.data?.let {
                                commentList.addAll(it.comments.toCommentItemTypeOneModelList())
                                setupRecyclerView()
                            }
                        }
                        TimePassBaseResult.Status.ERROR -> {
                            hideLoadingText()
                            categoryListResponse.message?.toast(ctxt)
                        }
                    }
                })
        }

    }

    private fun hideLoadingText() {
        binding.tvMessage.visible(false)
    }

    private fun setupRecyclerView() {
        binding.rvCommentList.apply {
            setHasFixedSize(true)
            adapter = CommentAdapter(commentList)
            scrollEnd()
        }
    }


    private fun notifyDataPosition(index: Int, railItemTypeTwoModel: RailItemTypeTwoModel) {
        binding.rvCommentList.adapter?.notifyItemChanged(index, railItemTypeTwoModel)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun scrollEnd() {
        binding.rvCommentList.smoothScrollToPosition(commentList.size)
    }
}