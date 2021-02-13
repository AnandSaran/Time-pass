package com.gregantech.timepass.view.createvideo.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.ahmedbadereldin.videotrimmer.Utility
import com.ahmedbadereldin.videotrimmer.customVideoViews.BackgroundTask
import com.ahmedbadereldin.videotrimmer.customVideoViews.BarThumb
import com.ahmedbadereldin.videotrimmer.customVideoViews.CustomRangeSeekBar
import com.ahmedbadereldin.videotrimmer.customVideoViews.OnRangeSeekBarChangeListener
import com.daasuu.mp4compose.FillMode
import com.daasuu.mp4compose.composer.Mp4Composer
import com.daasuu.mp4compose.filter.GlFilterGroup
import com.daasuu.mp4compose.filter.GlWatermarkFilter
import com.gregantech.timepass.R
import com.gregantech.timepass.base.TimePassBaseActivity
import com.gregantech.timepass.databinding.ActivityVideoTrimBinding
import com.gregantech.timepass.general.bundklekey.CreateVideoBundleEnum
import kotlinx.android.synthetic.main.activity_video_trim.*
import java.io.File
import java.util.*

class VideoTrimmerActivity : TimePassBaseActivity(),
    View.OnClickListener {
    private lateinit var binding: ActivityVideoTrimBinding

    private var mDuration = 0
    private var mTimeVideo = 0
    private var mStartPosition = 0
    private var mEndPosition = 0

    // set your max video trim seconds
    private val mMaxDuration = 30
    private val mHandler = Handler()
    var srcFile: String? = null
    var dstFile: String? = null


    companion object {
        fun generateIntent(
            context: Context, videoPath: String
        ): Intent {
            val intent = Intent(context, VideoTrimmerActivity::class.java)
            intent.putExtra(CreateVideoBundleEnum.VIDEO_PATH.value, videoPath)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_video_trim)

        if (intent.extras != null) {
            srcFile = intent.extras!!.getString(CreateVideoBundleEnum.VIDEO_PATH.value)
        }
        dstFile = (Environment.getExternalStorageDirectory()
            .toString() + "/" + getString(R.string.app_name) + Date().time
                + Utility.VIDEO_FORMAT)
        timeLineView.post(Runnable {
            setBitmap(Uri.parse(srcFile))
            videoView.setVideoURI(Uri.parse(srcFile))
        })
        setupToolBar()
        videoView.setOnPreparedListener { mp: MediaPlayer -> onVideoPrepared(mp) }
        videoView.setOnCompletionListener { onVideoCompleted() }

        // handle your range seekbar changes
        timeLineBar.addOnRangeSeekBarListener(object : OnRangeSeekBarChangeListener {
            override fun onCreate(
                customRangeSeekBarNew: CustomRangeSeekBar,
                index: Int,
                value: Float
            ) {
                // Do nothing
            }

            override fun onSeek(
                customRangeSeekBarNew: CustomRangeSeekBar,
                index: Int,
                value: Float
            ) {
                onSeekThumbs(index, value)
            }

            override fun onSeekStart(
                customRangeSeekBarNew: CustomRangeSeekBar,
                index: Int,
                value: Float
            ) {
                if (videoView != null) {
                    mHandler.removeCallbacks(mUpdateTimeTask)
                    seekBarVideo.progress = 0
                    videoView!!.seekTo(mStartPosition * 1000)
                    videoView!!.pause()
                    imgPlay.setBackgroundResource(R.drawable.ic_white_play)
                }
            }

            override fun onSeekStop(
                customRangeSeekBarNew: CustomRangeSeekBar,
                index: Int,
                value: Float
            ) {
                onStopSeekThumbs()
            }
        })
        imgPlay.setOnClickListener(this)

        // handle changes on seekbar for video play
        seekBarVideo.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar,
                i: Int,
                b: Boolean
            ) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                if (videoView != null) {
                    mHandler.removeCallbacks(mUpdateTimeTask)
                    seekBarVideo.max = mTimeVideo * 1000
                    seekBarVideo.progress = 0
                    videoView!!.seekTo(mStartPosition * 1000)
                    videoView!!.pause()
                    imgPlay.setBackgroundResource(R.drawable.ic_white_play)
                }
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                mHandler.removeCallbacks(mUpdateTimeTask)
                videoView.seekTo(mStartPosition * 1000 - seekBarVideo.progress)
            }
        })
    }

    override fun onClick(view: View) {
        if (view === imgPlay) {
            if (videoView!!.isPlaying) {
                if (videoView != null) {
                    videoView!!.pause()
                    imgPlay!!.setBackgroundResource(R.drawable.ic_white_play)
                }
            } else {
                if (videoView != null) {
                    videoView!!.start()
                    imgPlay!!.setBackgroundResource(R.drawable.ic_white_pause)
                    if (seekBarVideo!!.progress == 0) {
                        txtVideoLength!!.text = "00:00"
                        updateProgressBar()
                    }
                }
            }
        }
    }

    private fun setBitmap(mVideoUri: Uri) {
        timeLineView!!.setVideo(mVideoUri)
    }

    private fun onVideoPrepared(mp: MediaPlayer) {
        // Adjust the size of the video
        // so it fits on the screen
        //TODO manage proportion for video
        /*int videoWidth = mp.getVideoWidth();
        int videoHeight = mp.getVideoHeight();
        float videoProportion = (float) videoWidth / (float) videoHeight;
        int screenWidth = llVideoView.getWidth();
        int screenHeight = llVideoView.getHeight();
        float screenProportion = (float) screenWidth / (float) screenHeight;
        ViewGroup.LayoutParams lp = videoView.getLayoutParams();

        if (videoProportion > screenProportion) {
            lp.width = screenWidth;
            lp.height = (int) ((float) screenWidth / videoProportion);
        } else {
            lp.width = (int) (videoProportion * (float) screenHeight);
            lp.height = screenHeight;
        }
        videoView.setLayoutParams(lp);*/
        mDuration = videoView!!.duration / 1000
        setSeekBarPosition()
    }

    private fun updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100)
    }

    private val mUpdateTimeTask: Runnable = object : Runnable {
        override fun run() {
            if (seekBarVideo!!.progress >= seekBarVideo!!.max) {
                seekBarVideo!!.progress = videoView!!.currentPosition - mStartPosition * 1000
                val videoLength = milliSecondsToTimer(seekBarVideo!!.progress.toLong()) + ""
                txtVideoLength!!.text = videoLength
                videoView!!.seekTo(mStartPosition * 1000)
                videoView!!.pause()
                seekBarVideo!!.progress = 0
                txtVideoLength!!.text = "00:00"
                imgPlay!!.setBackgroundResource(R.drawable.ic_white_play)
            } else {
                seekBarVideo!!.progress = videoView!!.currentPosition - mStartPosition * 1000
                val videoLength = milliSecondsToTimer(seekBarVideo!!.progress.toLong()) + ""
                txtVideoLength!!.text = videoLength
                mHandler.postDelayed(this, 100)
            }
        }
    }

    private fun setSeekBarPosition() {
        if (mDuration >= mMaxDuration) {
            mStartPosition = 0
            mEndPosition = mMaxDuration
            timeLineBar!!.setThumbValue(0, mStartPosition * 100 / mDuration.toFloat())
            timeLineBar!!.setThumbValue(1, mEndPosition * 100 / mDuration.toFloat())
        } else {
            mStartPosition = 0
            mEndPosition = mDuration
        }
        mTimeVideo = mDuration
        timeLineBar!!.initMaxWidth()
        seekBarVideo!!.max = mMaxDuration * 1000
        videoView!!.seekTo(mStartPosition * 1000)
        var mStart = mStartPosition.toString() + ""
        if (mStartPosition < 10) mStart = "0$mStartPosition"
        val startMin = mStart.toInt() / 60
        val startSec = mStart.toInt() % 60
        var mEnd = mEndPosition.toString() + ""
        if (mEndPosition < 10) mEnd = "0$mEndPosition"
        val endMin = mEnd.toInt() / 60
        val endSec = mEnd.toInt() % 60
        txtVideoTrimSeconds!!.text = String.format(
            Locale.US,
            "%02d:%02d - %02d:%02d",
            startMin,
            startSec,
            endMin,
            endSec
        )
    }

    /**
     * called when playing video completes
     */
    private fun onVideoCompleted() {
        mHandler.removeCallbacks(mUpdateTimeTask)
        seekBarVideo!!.progress = 0
        videoView!!.seekTo(mStartPosition * 1000)
        videoView!!.pause()
        imgPlay!!.setBackgroundResource(R.drawable.ic_white_play)
    }

    /**
     * Handle changes of left and right thumb movements
     *
     * @param index index of thumb
     * @param value value
     */
    private fun onSeekThumbs(index: Int, value: Float) {
        when (index) {
            BarThumb.LEFT -> {
                mStartPosition = (mDuration * value / 100L).toInt()
                videoView!!.seekTo(mStartPosition * 1000)
            }
            BarThumb.RIGHT -> {
                mEndPosition = (mDuration * value / 100L).toInt()
            }
        }
        mTimeVideo = mEndPosition - mStartPosition
        seekBarVideo!!.max = mTimeVideo * 1000
        seekBarVideo!!.progress = 0
        videoView!!.seekTo(mStartPosition * 1000)
        var mStart = mStartPosition.toString() + ""
        if (mStartPosition < 10) mStart = "0$mStartPosition"
        val startMin = mStart.toInt() / 60
        val startSec = mStart.toInt() % 60
        var mEnd = mEndPosition.toString() + ""
        if (mEndPosition < 10) mEnd = "0$mEndPosition"
        val endMin = mEnd.toInt() / 60
        val endSec = mEnd.toInt() % 60
        txtVideoTrimSeconds!!.text = String.format(
            Locale.US,
            "%02d:%02d - %02d:%02d",
            startMin,
            startSec,
            endMin,
            endSec
        )
    }

    private fun onStopSeekThumbs() {
//        mMessageHandler.removeMessages(SHOW_PROGRESS);
//        videoView.pause();
//        mPlayView.setVisibility(View.VISIBLE);
    }

    fun milliSecondsToTimer(milliseconds: Long): String {
        var finalTimerString = ""
        val secondsString: String
        val minutesString: String
        val hours = (milliseconds / (1000 * 60 * 60)).toInt()
        val minutes = (milliseconds % (1000 * 60 * 60)).toInt() / (1000 * 60)
        val seconds = (milliseconds % (1000 * 60 * 60) % (1000 * 60) / 1000).toInt()
        // Add hours if there
        if (hours > 0) {
            finalTimerString = "$hours:"
        }

        // Prepending 0 to seconds if it is one digit
        secondsString = if (seconds < 10) {
            "0$seconds"
        } else {
            "" + seconds
        }
        minutesString = if (minutes < 10) {
            "0$minutes"
        } else {
            "" + minutes
        }
        finalTimerString = "$finalTimerString$minutesString:$secondsString"

        // return timer string
        return finalTimerString
    }

    private fun setupToolBar() {
        setSupportActionBar(binding.tbTrimVideo.toolbar)
        setToolbarTitle()
        setToolbarBackButton()
    }

    private fun setToolbarTitle() {
        supportActionBar?.title = getString(R.string.label_edit_video)
    }

    private fun setToolbarBackButton() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_video_trim, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.miNext -> {
                trimVideo()
                true
            }
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun trimVideo() {
        val diff = mEndPosition - mStartPosition
        if (diff < 3) {
            Toast.makeText(
                this@VideoTrimmerActivity, getString(R.string.video_length_validation),
                Toast.LENGTH_LONG
            ).show()
        } else {
            val mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(
                this@VideoTrimmerActivity,
                Uri.parse(srcFile)
            )
            val file = File(srcFile)
            //notify that video trimming started
            BackgroundTask.execute(object : BackgroundTask.Task("", 0L, "") {
                override fun execute() {
                    try {
                        val icon = BitmapFactory.decodeResource(
                            resources,
                            R.drawable.water_mark
                        )
                        trimVideo(
                            srcFile!!,
                            dstFile!!,
                            mStartPosition * 1000.toLong(),
                            mEndPosition * 1000.toLong(),
                            icon,
                            randomLetter()
                        )
                    } catch (e: Throwable) {
                        Thread.getDefaultUncaughtExceptionHandler()
                            .uncaughtException(Thread.currentThread(), e)
                    }
                }
            }
            )
        }
    }

    fun trimVideo(
        srcMp4Path: String,
        destMp4Path: String,
        trimStartMs: Long,
        trimEndMs: Long,
        icon: Bitmap,
        position: GlWatermarkFilter.Position
    ) {
        Mp4Composer(srcMp4Path, destMp4Path)
            .fillMode(FillMode.PRESERVE_ASPECT_FIT)
            .trim(trimStartMs, trimEndMs)
            .filter(GlFilterGroup(GlWatermarkFilter(icon, position)))
            .listener(object : Mp4Composer.Listener {
                override fun onFailed(exception: java.lang.Exception?) {
                    runOnUiThread { dismissProgressBar() }
                }

                override fun onProgress(progress: Double) {
                    runOnUiThread { showProgressBar() }
                }

                override fun onCanceled() {
                    runOnUiThread { dismissProgressBar() }
                }

                override fun onCurrentWrittenVideoTime(timeUs: Long) {
                }

                override fun onCompleted() {
                    runOnUiThread {
                        dismissProgressBar()
                        dismissProgressBar()
                        val conData = Bundle()
                        conData.putString("INTENT_VIDEO_FILE", destMp4Path)
                        val intent = Intent()
                        intent.putExtras(conData)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                }

            }).start()
    }

    private fun randomLetter(): GlWatermarkFilter.Position {
        val pick = Random().nextInt(GlWatermarkFilter.Position.values().size)
        return GlWatermarkFilter.Position.LEFT_TOP/*GlWatermarkFilter.Position.values()[pick]*/
    }
}