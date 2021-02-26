package app.tek4tv.tek4tvpublishing.ui

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.paging.insertFooterItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.tek4tv.tek4tvpublishing.R
import app.tek4tv.tek4tvpublishing.model.HeaderData
import app.tek4tv.tek4tvpublishing.model.Video
import app.tek4tv.tek4tvpublishing.model.VideoDetail
import app.tek4tv.tek4tvpublishing.viewmodel.VideoPlayerViewModel
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.util.Util
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_video_player.*
import kotlinx.android.synthetic.main.exo_player_control_view.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VideoPlayerActivity : AppCompatActivity()
{
    private var player: SimpleExoPlayer? = null
    private lateinit var videoView: PlayerView


    private val videosAdapter = VideoPagingAdapter()

    private val viewModel: VideoPlayerViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        setupRecycleView()

        //if(viewModel.curVideo == null)
        val videoData = intent.getSerializableExtra(VIDEO_KEY)!! as Video

        viewModel.headerData = videoData.run {
            HeaderData(title,createDate,playlist.name,description)
        }

        viewModel.videoId = videoData.media.id

        initButtons()

        videoView = findViewById(R.id.video_view)

        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            hideSystemUi()
            videoView.layoutParams.height = ConstraintLayout.LayoutParams.MATCH_PARENT
            btnRotate.setImageResource(R.drawable.ic_baseline_fullscreen_exit_24)
        } else
        {
            showSystemUi()
            videoView.layoutParams.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
            btnRotate.setImageResource(R.drawable.ic_baseline_fullscreen_24)
        }

        viewModel.curVideo.observe(this)
        {
            playVideo(viewModel.curVideo.value!!)
        }

        lifecycleScope.launch {
            viewModel.pagingData.collectLatest {
                videosAdapter.submitData(it)
            }
        }
    }

    override fun onStart()
    {
        super.onStart()
        if (Util.SDK_INT >= 24)
        {
            initPlayer()
        }
    }

    override fun onStop()
    {
        super.onStop()
        if (Util.SDK_INT >= 24)
        {
            releasePlayer();
        }
    }

    override fun onResume()
    {
        super.onResume()
        if (Util.SDK_INT < 24)
        {
            initPlayer()
        }
    }

    override fun onPause()
    {
        super.onPause()
        if (Util.SDK_INT < 24)
        {
            releasePlayer()
        }
    }

    private fun initButtons()
    {
        btnRotate.setOnClickListener {
            val orientation = resources.configuration.orientation
            requestedOrientation =
                    if (orientation == Configuration.ORIENTATION_LANDSCAPE)
                    {
                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    } else
                    {
                        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    }
        }
    }

    private fun hideSystemUi()
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        {
            window.insetsController?.hide(WindowInsets.Type.statusBars())

        } else
        {
            // hide status bar
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_IMMERSIVE or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }

    }

    private fun showSystemUi()
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        {
            window.insetsController?.show(WindowInsets.Type.statusBars())
        } else
        {
            // Show status bar
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.decorView.systemUiVisibility = SYSTEM_UI_FLAG_LAYOUT_STABLE
        }

    }

    private fun initPlayer()
    {
        player = SimpleExoPlayer.Builder(this).build()
        videoView.player = player
        player?.playWhenReady = true

    }

    private fun releasePlayer()
    {
        if (player != null)
        {
            viewModel.apply {
                playbackPosition = player?.currentPosition!!
                currentWindow = player?.currentWindowIndex!!
            }
            player?.release()
            player = null
        }
    }

    private fun setupRecycleView()
    {
        //videosAdapter.videos = viewModel.videoList
        videosAdapter.videoClickListener = {
            val intent = Intent(applicationContext, VideoPlayerActivity::class.java)
            intent.putExtra(VIDEO_KEY, it)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
        rv_video_list.adapter = videosAdapter
        rv_video_list.layoutManager = LinearLayoutManager(this)
    }

    private fun playVideo(video: VideoDetail?)
    {
        if (video == null) return

        //if(video.id != viewModel.curVideo?.id)
        viewModel.resetVideoParams()

        val mediaItem = MediaItem.fromUri("https://vodovp.tek4tv.vn/${video.path}")
        //txt_vid_name.text = video.name
        player?.apply {
            setMediaItem(mediaItem)
            seekTo(viewModel.currentWindow, viewModel.playbackPosition)
            prepare()
        }
        //viewModel.curVideo = video
        //videosAdapter.videos = viewModel.videoList.filter { it.id != video.id }
    }

    companion object
    {
        const val VIDEO_KEY = "videos_key"
    }
}