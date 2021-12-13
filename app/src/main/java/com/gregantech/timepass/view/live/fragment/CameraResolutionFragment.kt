package com.gregantech.timepass.view.live.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.gregantech.timepass.R
import com.gregantech.timepass.databinding.LayoutCameraResolutionsBinding
import io.antmedia.android.broadcaster.utils.Resolution

class CameraResolutionFragment : DialogFragment(), AdapterView.OnItemClickListener {

    private lateinit var binding : LayoutCameraResolutionsBinding    
    private val CAMERA_RESOLUTIONS = "CAMERA_RESOLUTIONS"
    private val SELECTED_SIZE_WIDTH = "SELECTED_SIZE_WIDTH"
    private val SELECTED_SIZE_HEIGHT = "SELECTED_SIZE_HEIGHT"
    private var cameraResolutionListView : ListView?=null
    private var cameraResolutionAdapter = CameResolutionsAdapter()
    private var cameraResolutions : ArrayList<Resolution?>?=null
    private var selectedSizeWidth = 0
    private var selectedSizeHeight = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        restoreState(savedInstanceState)
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.layout_camera_resolutions,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.cameraResolutionsListview.apply {
            adapter = cameraResolutionAdapter
            onItemClickListener = this@CameraResolutionFragment
            choiceMode = ListView.CHOICE_MODE_SINGLE
        }
    }

    fun setCameraResolutions(
        camResolution: ArrayList<Resolution?>,
        selectedSize: Resolution
    ) {
        this.cameraResolutions = camResolution
        this.selectedSizeWidth = selectedSize.width
        this.selectedSizeHeight = selectedSize.height
        cameraResolutionAdapter.setCameResolutions(cameraResolutions)
    }

    private fun setCameraResolution(size: Resolution){
     /*   if (activity is LiveVideoBroadCastActivity){
            (activity as LiveVideoBroadCastActivity?)?.setResolution(size)
        }*/
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        with(outState){
            putSerializable(CAMERA_RESOLUTIONS, cameraResolutions)
            putInt(SELECTED_SIZE_WIDTH, selectedSizeWidth)
            putInt(SELECTED_SIZE_HEIGHT, selectedSizeHeight)
        }
    }

    private fun restoreState(savedInstanceState: Bundle?) {
        savedInstanceState?.run {
            if (containsKey(CAMERA_RESOLUTIONS)) {
                cameraResolutions = getSerializable(CAMERA_RESOLUTIONS) as ArrayList<Resolution?>?
            }
            if (containsKey(SELECTED_SIZE_WIDTH) &&
                containsKey(SELECTED_SIZE_WIDTH)
            ) {
                selectedSizeWidth = getInt(SELECTED_SIZE_WIDTH)
                selectedSizeHeight = getInt(SELECTED_SIZE_HEIGHT)
            }
            cameraResolutionAdapter.setCameResolutions(cameraResolutions)
        }
    }


    override fun onItemClick(p0: AdapterView<*>?, p1: View?, i: Int, l: Long) {
        cameraResolutionAdapter.getItem(i)?.let {
            setCameraResolution(it)
        }
    }
    
    internal inner class CameResolutionsAdapter : BaseAdapter() {
        var mcameraResolutions: ArrayList<Resolution?>? = null
        fun setCameResolutions(cameraResolutions: ArrayList<Resolution?>?) {
            mcameraResolutions = cameraResolutions
        }

        override fun getCount(): Int {
            return mcameraResolutions!!.size
        }

        override fun getItem(i: Int): Resolution? {
            //reverse order. Highest resolution is at top
            return mcameraResolutions?.get(count - 1 - i)
        }

        override fun getItemId(i: Int): Long {
            return i.toLong()
        }

        override fun getView(i: Int, cv: View?, viewGroup: ViewGroup?): View? {
            var convertView = cv
            val holder: ViewHolder

            if (convertView == null) {
                convertView = LayoutInflater.from(activity).inflate(
                    android.R.layout.simple_list_item_single_choice,
                    null
                )
                holder = ViewHolder()
                with(convertView){
                    holder.resolutionText =
                        findViewById<View>(android.R.id.text1) as TextView
                    tag = holder
                }
            } else {
                holder = convertView.tag as ViewHolder
            }

            //reverse order. Highest resolution is at top
            val size = getItem(i)
            if (size?.width == selectedSizeWidth &&
                size.height == selectedSizeHeight
            ) {
                run { cameraResolutionListView?.setItemChecked(i, true) }
            }
            val resolutionText = size?.width.toString() + " x " + size?.height

            Log.d("CameraResolution", "getView: resolutionText $resolutionText")

            // adding auto resolution adding it to the first
            holder.resolutionText!!.text = resolutionText
            return convertView
        }

        inner class ViewHolder {
            var resolutionText: TextView? = null
        }
    }
}