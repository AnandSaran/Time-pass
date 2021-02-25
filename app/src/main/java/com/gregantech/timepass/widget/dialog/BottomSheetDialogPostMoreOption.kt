package com.gregantech.timepass.widget.dialog

import android.content.Context
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.gregantech.timepass.databinding.BottomSheetPostMoreBinding
import com.gregantech.timepass.general.PostMoreOptionNavigationEnum
import com.gregantech.timepass.general.content.PostMoreOptionContentModel
import com.gregantech.timepass.util.extension.visible

class BottomSheetDialogPostMoreOption(
    context: Context,
    private val contentModel: PostMoreOptionContentModel,
    private val navigationButtonAction: ((data: Any, postMoreOptionNavigationEnum: PostMoreOptionNavigationEnum) -> Unit)?,
    private val data: Any
) : BottomSheetDialog(context) {
    private lateinit var binding: BottomSheetPostMoreBinding

    init {
        createDialog()
    }

    private fun createDialog() {
        val layoutInflater = LayoutInflater.from(context)
        binding = BottomSheetPostMoreBinding.inflate(layoutInflater, null, false)
        setContentView(binding.root)
        setupOnClick()
        setupView()
    }

    private fun setupView() {
        binding.tvEdit.visible(contentModel.isShowEdit)
        binding.tvDownload.visible(contentModel.isShowDownload)
        binding.tvDelete.visible(contentModel.isShowDelete)
    }


    private fun setupOnClick() {
        binding.tvDelete.setOnClickListener {
            navigationButtonAction?.invoke(data, PostMoreOptionNavigationEnum.NAVIGATION_DELETE)
            dismissDialog()
        }
        binding.tvDownload.setOnClickListener {
            navigationButtonAction?.invoke(data, PostMoreOptionNavigationEnum.NAVIGATION_DOWNLOAD)
            dismissDialog()
        }
        binding.tvEdit.setOnClickListener {
            navigationButtonAction?.invoke(data, PostMoreOptionNavigationEnum.NAVIGATION_EDIT)
            dismissDialog()
        }
        binding.tvCancel.setOnClickListener {
            dismissDialog()
        }
    }

    private fun dismissDialog() {
        super.dismiss()
    }
}