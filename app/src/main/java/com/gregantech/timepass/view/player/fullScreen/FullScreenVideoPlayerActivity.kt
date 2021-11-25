package com.gregantech.timepass.view.player.fullScreen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.gregantech.timepass.R
import com.gregantech.timepass.databinding.ActivityFullScreenVideoPlayerBinding
import com.gregantech.timepass.util.DataUtil
import com.gregantech.timepass.util.extension.toast

class FullScreenVideoPlayerActivity : AppCompatActivity() {


    private lateinit var binding: ActivityFullScreenVideoPlayerBinding
    private var fullScreenVideoPlayer: FullScreenVideoAdapter? = null
    private var currentPage = 1
    private var totalPage = 2
    private var totalSize = 5

    companion object {

        fun present(context: Context) {
            val intent = Intent(context, FullScreenVideoPlayerActivity::class.java)
            context.startActivity(intent)
        }

        fun generateIntent(context: Context, videoUrl: String, videoPosition: Long): Intent {
            val intent = Intent(context, FullScreenVideoPlayerActivity::class.java)
            context.startActivity(intent)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_full_screen_video_player)
        initPlayer()
        setAssets()
    }

    private fun setAssets() {

    }

    private fun initPlayer() {
        val limit = 5
        val vidList = DataUtil.videoList(this, 1, limit)
        printList(vidList, 1)
        fullScreenVideoPlayer = FullScreenVideoAdapter(vidList, ::tikTokCallBack)
        PlayerProvider.createPlayers(this, vidList)
        binding.rvTikTok.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = fullScreenVideoPlayer
            addOnScrollListener(object : PagerScrollHandler(binding.rvTikTok) {
                override fun getCurrentPagePosition(): Int {
                    return doLoadMore(currentPosition, limit)
                }
            })
        }
    }

    private fun doLoadMore(position: Int, limit: Int): Int {
        val currentPos = PlayerProvider.getAbsolutePosition(position)
        Log.d("doLoadMore", "currentPos $currentPos limit $limit currentPage $currentPage")
        if (currentPos == (limit * currentPage) - 2) {
            if (currentPage != totalPage) {
                "Do Load more".toast(this)
                val vidList = DataUtil.videoList(this, 2, limit)
                printList(vidList, 2)
                fullScreenVideoPlayer?.addMore(vidList)
                PlayerProvider.createPlayers(this, vidList)
                currentPage = totalPage
            }
        }
        return position
    }

    private fun printList(vidList: ArrayList<TikTokModel>, type: Int) {
        vidList.forEach {
            Log.d("FullScreenCrap", "title ${it.title} url ${it.sourceUrl}")
        }
    }

    private fun tikTokCallBack(tiktokModel: TikTokModel, type: String) {
        type.plus(" clicked").toast(this)
    }

    override fun onStart() {
        super.onStart()
        PlayerProvider.resumePlayer()
    }

    override fun onStop() {
        super.onStop()
        PlayerProvider.stopPlayer()
    }

    override fun onDestroy() {
        PlayerProvider.releasePlayer()
        super.onDestroy()
    }

}