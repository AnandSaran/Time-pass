package com.gregantech.timepass.view.tiktok.fragment

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.gregantech.timepass.R
import com.gregantech.timepass.base.TimePassBaseFragment
import com.gregantech.timepass.databinding.FragmentTikTokBinding
import com.gregantech.timepass.general.bundklekey.TikTokBundleKeyEnum
import com.gregantech.timepass.model.RailItemTypeTwoModel
import com.gregantech.timepass.model.getStrippedFileName
import com.gregantech.timepass.network.repository.VideoListRepository
import com.gregantech.timepass.network.repository.bridge.toRailItemTypeTwoModel
import com.gregantech.timepass.network.response.Video
import com.gregantech.timepass.util.PlayerUtil.buildMediaSources
import com.gregantech.timepass.util.constant.VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
import com.gregantech.timepass.util.extension.*
import com.gregantech.timepass.util.sharedpreference.SharedPreferenceHelper
import com.gregantech.timepass.view.comment.fragment.CommentActivity
import com.gregantech.timepass.view.profile.activity.UserProfileActivity
import com.gregantech.timepass.view.profile.viewmodel.UserVideoListActivityViewModel
import com.gregantech.timepass.widget.PlayPauseView
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.include_fullscreen_vid_options.view.*

class TikTokFragment : TimePassBaseFragment() {

    private lateinit var binding: FragmentTikTokBinding
    private val videoModel by lazy { arguments?.getParcelable<Video>(TikTokBundleKeyEnum.VIDEO_DATA.value) }
    private lateinit var activityViewModelFactory: UserVideoListActivityViewModel.Factory

    private var simplePlayer: SimpleExoPlayer? = null
    var currentItem: RailItemTypeTwoModel? = null

    var playingPosition = 0L
    private var downloadID: Long? = null
    private var isShareClick = false
    private var isRegistered = false

    private val viewModel: UserVideoListActivityViewModel by lazy {
        requireNotNull(this) {
            VIEW_MODEL_IN_ACCESSIBLE_MESSAGE
        }
        ViewModelProvider(
            this,
            activityViewModelFactory
        ).get(UserVideoListActivityViewModel::class.java)
    }


    companion object {
        fun newInstance(video: Video, seekPosition: Long) = TikTokFragment()
            .apply {
                arguments = Bundle().apply {
                    putParcelable(TikTokBundleKeyEnum.VIDEO_DATA.value, video)
                    putLong(TikTokBundleKeyEnum.SEEK_POSITION.value, seekPosition)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tik_tok, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getInputs()
        setAssets()
        subscribeToObservers()
    }

    private fun getInputs() {
        playingPosition = arguments?.getLong(TikTokBundleKeyEnum.SEEK_POSITION.value) ?: 0L
    }

    private fun initViewModel() {
        activityViewModelFactory = UserVideoListActivityViewModel.Factory(
            VideoListRepository(),
            SharedPreferenceHelper
        )
    }

    private fun setAssets() {
        val simplePlayer = getPlayer()
        binding.fullScreenPlayer.player = simplePlayer
        videoModel?.run {
            binding.includedMoreOption.ivProfilePicture.loadUrlCircle(
                userImage,
                R.drawable.place_holder_profile
            )
            handleLikeState(isLiked, videoLikes)
            handleCommentCount(videoComments)
            binding.tvName.text = videoTitle
            binding.tvDesc.text = videoDescription
            prepareMedia(videoName) // start playing
        }

        binding.tiktokRoot.setOnClickListener(onClicks)
        //binding.ivPausePlay.setOnClickListener(onClicks)
        with(binding.includedMoreOption) {
            arrayOf(ivProfilePicture, ivLikeVid, ivComment, ivShare, ivDownload).forEach {
                it.setOnClickListener(onClicks)
            }
        }
    }

    private fun subscribeToObservers() {
        viewModel.downloadRequest.observe(viewLifecycleOwner, Observer {
            downloadVideo(it)
        })
    }

    private val onClicks = View.OnClickListener {
        when (it) {
            binding.tiktokRoot -> {
                if (simplePlayer?.isPlaying == true) {
                    binding.ivPausePlay.setState(PlayPauseView.STATE_PAUSE)
                    pauseVideo(false)
                } else {
                    playVideo()
                    binding.ivPausePlay.setState(PlayPauseView.STATE_PLAY)
                }
            }
            binding.includedMoreOption.ivProfilePicture -> {
                videoModel?.followerId?.let {
                    UserProfileActivity.present(requireContext(), it)
                }
            }
            binding.includedMoreOption.ivLikeVid -> {
                videoModel?.run {
                    isLiked = !isLiked
                    setVideoLikeUnLike(Id, isLiked)
                    videoLikes = if (isLiked) videoLikes + 1 else videoLikes - 1
                    handleLikeState(isLiked, videoLikes)
                }
                currentItem = videoModel?.toRailItemTypeTwoModel()
            }
            binding.includedMoreOption.ivComment -> {
                startCommentForResult.launch(
                    CommentActivity.generateIntent(
                        requireContext(),
                        videoModel?.Id!!,
                        isUserPost = true
                    )
                )
            }
            binding.includedMoreOption.ivShare -> {
                currentItem = videoModel?.toRailItemTypeTwoModel()
                isShareClick = true
                askPermission()
            }
            binding.includedMoreOption.ivDownload -> {
                currentItem = videoModel?.toRailItemTypeTwoModel()
                isShareClick = false
                askPermission()
            }
        }
    }

    private val startCommentForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let {
                    val commentCount = it.getIntExtra(
                        TikTokBundleKeyEnum.COMMENT_COUNT.value, -1
                    )
                    if (commentCount != -1) {
                        binding.includedMoreOption.tvCommentCount.text = commentCount.toString()
                    }
                }
            }
        }

    private fun handleLikeState(isLiked: Boolean, videoLikes: Int) {
        Log.d(TAG, "handleLikeState: isLiked $isLiked")
        val resId = if (isLiked) R.drawable.ic_like_green else R.drawable.ic_un_like
        binding.includedMoreOption.ivLikeVid.setImageResource(resId)
        binding.includedMoreOption.tvLikeCount.text = videoLikes.toString()
    }

    private fun handleCommentCount(videoComments: Int) {
        binding.includedMoreOption.tvCommentCount.text = videoComments.toString()
    }

    private fun askPermission() {
        TedPermission.with(requireContext())
            .setPermissionListener(permissionListener)
            .setDeniedMessage(getString(R.string.permission_denied_message))
            .setPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .check()
    }

    private var permissionListener: PermissionListener = object : PermissionListener {
        override fun onPermissionGranted() {
            onClickDownload(currentItem!!)
        }

        override fun onPermissionDenied(deniedPermissions: List<String>) {
        }
    }

    private fun onClickDownload(railModel: RailItemTypeTwoModel) {
        if (isNotDownloaded(requireContext(), railModel.getStrippedFileName(), isShareClick))
            viewModel.createDownloadRequest(railModel, getString(R.string.app_name))
    }

    private fun downloadVideo(request: DownloadManager.Request) {
        if (isShareClick) {
            showProgressBar()
        }
        getString(R.string.download_started).toast(requireContext())
        val manager =
            requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadID = manager.enqueue(request)
    }

    private val playerCallback: Player.EventListener = object : Player.EventListener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            Log.e("onPlayerStateChanged", "playbackState: $playbackState")
        }

        override fun onPlayerError(error: ExoPlaybackException) {
            super.onPlayerError(error)
            Log.e("onPlayerError", "error $error")
        }
    }

    private fun prepareVideoPlayer() {
        val loadControl =
            DefaultLoadControl.Builder().setBufferDurationsMs(32 * 1024, 64 * 1024, 1024, 1024)
                .createDefaultLoadControl()
        simplePlayer = SimpleExoPlayer.Builder(requireContext()).setLoadControl(loadControl).build()
    }

    private fun getPlayer(): SimpleExoPlayer? {
        if (simplePlayer == null) {
            prepareVideoPlayer()
        }
        return simplePlayer
    }

    private fun prepareMedia(linkUrl: String) {
        Log.d("TikTokFragment", "prepareMedia linkUrl: $linkUrl playingPosition $playingPosition")

        val mediaSource = buildMediaSources(requireContext(), linkUrl)

        simplePlayer?.apply {
            seekTo(getSeekToPos())
            prepare(mediaSource)
            repeatMode = Player.REPEAT_MODE_ONE
            playWhenReady = false
            muted = false
            addListener(playerCallback)
        }
    }

    private fun getSeekToPos(): Long {
        val tmp = playingPosition
        if (tmp > 0) {
            playingPosition = 0L
            return tmp
        }
        return 0L
    }

    private fun setVideoLikeUnLike(id: String, isLiked: Boolean) {
        viewModel.setVideoLike(id, isLiked)
            .observe(this, androidx.lifecycle.Observer {

            })
    }

    private fun setArtwork(drawable: Drawable, playerView: PlayerView) {
        playerView.useArtwork = true
        playerView.defaultArtwork = drawable
    }

    private fun playVideo() {
        simplePlayer?.playWhenReady = true
    }

    private fun restartVideo() {
        if (simplePlayer == null) {
            videoModel?.videoName?.let {
                prepareMedia(it)
            }
        } else {
            simplePlayer?.apply {
                muted = false
                seekTo(getSeekToPos())
                playWhenReady = true
            }
        }
    }

    private val downloadStatusReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadID == id && isShareClick) {
                requireContext().shareDownloadedFile(downloadID!!)
            }
            if (isShareClick) {
                dismissProgressBar()
            } else
                getString(R.string.download_completed).toast(requireContext())
        }
    }

    private fun pauseVideo(mute: Boolean) {
        simplePlayer?.apply {
            muted = mute
            playWhenReady = false
        }
    }

    private fun releasePlayer() {
        simplePlayer?.apply {
            stop(true)
            release()
        }
    }

    override fun onPause() {
        binding.ivPausePlay.gone()
        pauseVideo(true)
        super.onPause()
    }

    override fun onStart() {
        super.onStart()
        requireContext().registerReceiver(
            downloadStatusReceiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
        isRegistered = true
    }

    override fun onStop() {
        super.onStop()
        if (isRegistered)
            requireContext().unregisterReceiver(downloadStatusReceiver)
    }

    override fun onResume() {
        restartVideo()
        binding.ivPausePlay.show()
        super.onResume()
    }

    override fun onDestroy() {
        releasePlayer()
        super.onDestroy()
    }

}