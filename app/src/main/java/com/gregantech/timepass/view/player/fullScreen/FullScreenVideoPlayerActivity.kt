package com.gregantech.timepass.view.player.fullScreen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.gregantech.timepass.R
import com.gregantech.timepass.databinding.ActivityFullScreenVideoPlayerBinding
import com.gregantech.timepass.util.DataUtil

class FullScreenVideoPlayerActivity : AppCompatActivity() {


    private lateinit var binding: ActivityFullScreenVideoPlayerBinding
    private var fullScreenVideoPlayer: FullScreenVideoAdapter? = null

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
        initAssets()
    }

    private fun initAssets() {
        val vidList = DataUtil.videoList(this)
        fullScreenVideoPlayer = FullScreenVideoAdapter(vidList)
        PlayerProvider.createPlayers(this, vidList)
        binding.rvTikTok.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = fullScreenVideoPlayer
            addOnScrollListener(object : PagerScrollHandler(binding.rvTikTok) {
                override fun getCurrentPagePosition(): Int {
                    return currentPosition
                }
            })
        }
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