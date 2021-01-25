package com.gregantech.timepass.adapter.comment

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gregantech.timepass.model.*
import com.gregantech.timepass.viewholder.comment.CommentItemTypeOneViewHolder

/**
 * Created by anand
 * Recycle view adapter of comment.
 */
class CommentAdapter(
    private val listModel: ArrayList<CommentBaseItemModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TAG = this::class.java.simpleName

    override fun getItemCount(): Int {
        return listModel.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            CommentTypeEnum.TYPE_COMMENT_ITEM_ONE.value -> CommentItemTypeOneViewHolder.from(parent)
            else -> throw ClassCastException("$TAG - Unknown viewType: $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return listModel[position].commentTypeEnum.value
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        when (holder.itemViewType) {
            CommentTypeEnum.TYPE_COMMENT_ITEM_ONE.value -> {
                val commentTypeOneModel = listModel[position] as CommentTypeOneModel
                val mHolder = holder as CommentItemTypeOneViewHolder
                mHolder.bind(commentTypeOneModel)
            }
            else -> throw ClassCastException("$TAG - Unknown viewType: ${holder.itemViewType}")
        }
    }
}