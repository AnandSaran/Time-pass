package com.gregantech.timepass.adapter.rail

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.gregantech.timepass.adapter.handler.rail.RailItemClickHandler
import com.gregantech.timepass.model.RailBaseItemModel
import com.gregantech.timepass.model.RailItemTypeEnum
import com.gregantech.timepass.model.RailItemTypeOneModel
import com.gregantech.timepass.model.RailItemTypeTwoModel
import com.gregantech.timepass.viewholder.rail.RailItemTypeOneViewHolder
import com.gregantech.timepass.viewholder.rail.RailItemTypeTwoViewHolder

/**
 * Created by anand
 * Recycle view adapter of Rail.
 */
class RailAdapter(
    private val railListModel: ArrayList<RailBaseItemModel>,
    private val railItemClickHandler: RailItemClickHandler
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TAG = this::class.java.simpleName

    override fun getItemCount(): Int {
        return railListModel.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            RailItemTypeEnum.TYPE_RAIL_ITEM_ONE.value -> RailItemTypeOneViewHolder.from(parent)
            RailItemTypeEnum.TYPE_RAIL_ITEM_TWO.value -> RailItemTypeTwoViewHolder.from(parent)
            else -> throw ClassCastException("$TAG - Unknown viewType: $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return railListModel[position].railItemType.value
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        when (holder.itemViewType) {
            RailItemTypeEnum.TYPE_RAIL_ITEM_ONE.value -> {
                val railItem = railListModel[position] as RailItemTypeOneModel
                val mHolder = holder as RailItemTypeOneViewHolder
                mHolder.bind(railItem, railItemClickHandler)
            }
            RailItemTypeEnum.TYPE_RAIL_ITEM_TWO.value -> {
                val railItem = railListModel[position] as RailItemTypeTwoModel
                val mHolder = holder as RailItemTypeTwoViewHolder
                mHolder.bind(railItem, railItemClickHandler)
            }
            else -> throw ClassCastException("$TAG - Unknown viewType: ${holder.itemViewType}")
        }
    }
}


