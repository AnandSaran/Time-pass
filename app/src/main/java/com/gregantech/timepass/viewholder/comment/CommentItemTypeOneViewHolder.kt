package com.gregantech.timepass.viewholder.comment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gregantech.timepass.R
import com.gregantech.timepass.databinding.ItemCommentTypeOneBinding
import com.gregantech.timepass.model.CommentTypeOneModel
import com.gregantech.timepass.util.extension.loadUrlCircle
import com.gregantech.timepass.util.extension.toTime


/**
 * Created by anand
 */
class CommentItemTypeOneViewHolder(private var itemCommentTypeOneBinding: ItemCommentTypeOneBinding) :
    RecyclerView.ViewHolder(itemCommentTypeOneBinding.root) {

    fun bind(
        commentTypeOneModel: CommentTypeOneModel
    ) {
        itemCommentTypeOneBinding.apply {
            tvUserName.text = commentTypeOneModel.title
            tvComments.text = commentTypeOneModel.subtitle
            ivUserProfile.loadUrlCircle(commentTypeOneModel.image, R.drawable.place_holder_profile)
            tvCommentTime.text = commentTypeOneModel.time.toTime()
            executePendingBindings()
        }
    }

    companion object {
        fun from(parent: ViewGroup): CommentItemTypeOneViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemCommentTypeOneBinding.inflate(layoutInflater, parent, false)
            return CommentItemTypeOneViewHolder(binding)
        }
    }
}