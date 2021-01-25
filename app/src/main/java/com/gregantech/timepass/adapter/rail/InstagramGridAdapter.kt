package com.gregantech.timepass.adapter.rail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.Player
import com.gregantech.timepass.adapter.handler.rail.RailItemClickHandler
import com.gregantech.timepass.databinding.ItemInstagramGridBinding
import com.gregantech.timepass.model.RailBaseItemModel
import com.gregantech.timepass.model.RailItemTypeTwoModel
import com.gregantech.timepass.util.GridPlayerViewAdapter
import com.gregantech.timepass.util.PlayerStateCallback
import java.util.*

/**
 * A custom adapter to use with the RecyclerView widget.
 */
class InstagramGridAdapter(
    private var modelList: ArrayList<RailBaseItemModel>,
    private val railItemClickHandler: RailItemClickHandler
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), PlayerStateCallback {

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): VideoPlayerViewHolder {

        val layoutInflater = LayoutInflater.from(viewGroup.context)
        val binding = ItemInstagramGridBinding.inflate(layoutInflater, viewGroup, false)

        return VideoPlayerViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {

        //Here you can fill your row view
        if (holder is VideoPlayerViewHolder) {
            val model = getItem(position)

            // send data to view holder
            holder.onBind(model)
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        val position = holder.adapterPosition
        GridPlayerViewAdapter.releaseRecycledPlayers(position)
        super.onViewRecycled(holder)
    }

    override fun getItemCount(): Int {
        return modelList.size
    }

    private fun getItem(position: Int): RailItemTypeTwoModel {
        return modelList[position] as RailItemTypeTwoModel
    }


    inner class VideoPlayerViewHolder(private val binding: ItemInstagramGridBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(model: RailItemTypeTwoModel) {
            binding.apply {
                dataModel = model
                callback = this@InstagramGridAdapter
                index = adapterPosition
                //(clPlayer.layoutParams as ConstraintLayout.LayoutParams).dimensionRatio = "1:1"
                setupOnClick(model)
                executePendingBindings()
            }
        }


        private fun setupOnClick(railItem: RailItemTypeTwoModel) {
            binding.root.setOnClickListener {
                if (railItemClickHandler.isInitializedForPosterClicking()) {
                    railItemClickHandler.clickPoster(railItem)
                }
            }
        }
    }


    override fun onVideoDurationRetrieved(duration: Long, player: Player) {
    }

    override fun onVideoBuffering(player: Player) {
    }

    override fun onStartedPlaying(player: Player) {

    }

    override fun onFinishedPlaying(player: Player) {
    }
}