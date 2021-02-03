package com.gregantech.timepass.view.home.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.gregantech.timepass.R
import kotlinx.android.synthetic.main.bottom_sheet_file_picker.*

class FilePickerBottomSheetFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_file_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
    }

    private fun setUpViews() {
        tvImage.setOnClickListener {
            dismissAllowingStateLoss()
            mListener?.onItemClick(getString(R.string.image))
        }

        tvVideo.setOnClickListener {
            dismissAllowingStateLoss()
            mListener?.onItemClick(getString(R.string.video))
        }
    }

    private var mListener: ItemClickListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ItemClickListener) {
            mListener = context as ItemClickListener
        } else {
            throw RuntimeException(
                context.toString().toString() + " must implement ItemClickListener"
            )
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface ItemClickListener {
        fun onItemClick(item: String)
    }

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle): FilePickerBottomSheetFragment {
            val fragment = FilePickerBottomSheetFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}