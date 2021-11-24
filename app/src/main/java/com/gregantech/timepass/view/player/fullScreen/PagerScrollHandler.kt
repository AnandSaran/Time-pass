package com.gregantech.timepass.view.player.fullScreen

import android.view.View
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.Player

abstract class PagerScrollHandler(
    private val pager: RecyclerView
) : RecyclerView.OnScrollListener() {

    init {
        pager.post { playVideoAtPage(getCurrentPagePosition()) }
    }

    abstract fun getCurrentPagePosition(): Int

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        when (newState) {
            RecyclerView.SCROLL_STATE_IDLE -> {
                //animatePageScale(1.0f)
                playVideoAtPage(getCurrentPagePosition())
            }
            RecyclerView.SCROLL_STATE_DRAGGING -> {
                //animatePageScale(0.9f)
            }
            RecyclerView.SCROLL_STATE_SETTLING -> {
            }
        }
    }

    private fun animatePageScale(scale: Float) {
        for (page in pager.children) {
            if (page.scaleX != scale) {
                page.animate()
                    .scaleX(scale)
                    .scaleY(scale)
            }
        }
    }

    protected open fun playVideoAtPage(position: Int) {
        PlayerProvider.mutePlayer()

        pager.children
            .mapNotNull { pager.getChildViewHolder(it) }
            .filterIsInstance<FullScreenVideoAdapter.VideoViewHolder>()
            .onEach {
                it.progressView.removeAllViews()
                it.playerView.player = null
            }
            .forEach {
                if (it.adapterPosition == position) {
                    it.progressView.addView(PlayerProvider.getProgress(it.adapterPosition))
                    it.playerView.player = PlayerProvider.getPlayer(it.adapterPosition)?.apply {
                        muted = false
                        playWhenReady = true
                        addListener(object : Player.EventListener {

                            override fun onPlayerStateChanged(
                                playWhenReady: Boolean,
                                playbackState: Int
                            ) {
                                if (playbackState == Player.STATE_BUFFERING) {
                                    it.progressView?.show()
                                }

                                if (playbackState == Player.STATE_READY && playWhenReady) {
                                    it.progressView?.gone()
                                }

                            }
                        })
                    }
                }
            }
    }

}


fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}
