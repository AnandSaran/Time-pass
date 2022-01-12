package com.gregantech.timepass.adapter.interaction

import android.content.Context
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gregantech.timepass.R
import com.gregantech.timepass.databinding.ItemInteractionBinding
import com.gregantech.timepass.model.PostItem
import com.gregantech.timepass.util.extension.*

class InteractionAdapter(val callback: (PostItem) -> Unit) :
    RecyclerView.Adapter<InteractionAdapter.VH>() {

    var interactionList: ArrayList<PostItem?>? = null
        set(value) {
            field = value
            notifyItemRangeInserted(0, value!!.size)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemInteractionBinding.inflate(LayoutInflater.from(parent.context)))

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(interactionList?.get(position))
    }

    override fun getItemCount() = interactionList?.size ?: 0

    inner class VH(val binding: ItemInteractionBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.llayInteraction.setOnClickListener {
                callback.invoke(interactionList?.get(adapterPosition)!!)
            }
        }

        fun bind(postItem: PostItem?) {
            postItem?.run {
                binding.ivProfile.loadUrlCircle(
                    user?.activityProfileImage,
                    R.drawable.place_holder_profile
                )
                val pic = if (isImage == false) videoImage else image
                binding.ivContent.apply {
                    if (activityType == "Follow") gone() else show()
                }
                binding.ivContent.setImageRoundedCorner(pic, R.drawable.placeholder_add_user_pic)
                binding.tvActivity.text = getSpannableText(binding.root.context, this)
            }
        }

        private fun getSpannableText(ctx: Context, postItem: PostItem): SpannableString {

            with(postItem) {

                val name = user?.activityUserName ?: "Anonymous user"
                val comment = ativityTitle
                val time = timestamp?.toPrettyTime()

                return when (activityType) {
                    "Comments" -> {
                        val str = String.format("%s commented: %s\n%s", name, comment, time)
                        SpannableString(str).apply {
                            setCustomTypefaceSpanBold(R.font.roboto_medium, ctx, 0, name.length)
                            setForegroundColorSpan(R.color.black, ctx, 0, name.length)
                            setForegroundColorSpan(
                                R.color.black,
                                ctx,
                                str.indexOf(comment!!),
                                str.indexOf(comment) + comment.length
                            )
                            setAbsoluteSpan(
                                11,
                                str.indexOf(comment),
                                str.indexOf(comment) + comment.length
                            )
                            setForegroundColorSpan(
                                R.color.gray,
                                ctx,
                                str.indexOf(time!!),
                                str.indexOf(time) + time.length
                            )
                            setAbsoluteSpan(11, str.indexOf(time), str.indexOf(time) + time.length)
                        }
                    }
                    "Follow" -> {
                        val str = String.format("%s started following you.\n%s", name, time)
                        SpannableString(str).apply {
                            setCustomTypefaceSpanBold(R.font.roboto_medium, ctx, 0, name.length)
                            setForegroundColorSpan(R.color.black, ctx, 0, name.length)
                            setForegroundColorSpan(
                                R.color.gray,
                                ctx,
                                str.indexOf(time!!),
                                str.indexOf(time) + time.length
                            )
                            setAbsoluteSpan(11, str.indexOf(time), str.indexOf(time) + time.length)
                        }
                    }
                    else -> {
                        val str = String.format("%s liked your photo.\n%s", name, time)
                        SpannableString(str).apply {
                            setCustomTypefaceSpanBold(R.font.roboto_medium, ctx, 0, name.length)
                            setForegroundColorSpan(R.color.black, ctx, 0, name.length)
                            setForegroundColorSpan(
                                R.color.gray,
                                ctx,
                                str.indexOf(time!!),
                                str.indexOf(time) + time.length
                            )
                            setAbsoluteSpan(11, str.indexOf(time), str.indexOf(time) + time.length)
                        }
                    }
                }
            }
        }
    }


}