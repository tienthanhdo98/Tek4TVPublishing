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
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import app.tek4tv.tek4tvpublishing.R
import app.tek4tv.tek4tvpublishing.model.HeaderData
import app.tek4tv.tek4tvpublishing.model.Video
import app.tek4tv.tek4tvpublishing.model.VideoDetail
import app.tek4tv.tek4tvpublishing.network.Keyword
import app.tek4tv.tek4tvpublishing.viewmodel.VideoPlayerViewModel
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.util.Util
import com.google.android.material.chip.Chip
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_video_player.*
import kotlinx.android.synthetic.main.exo_player_control_view.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.reflect.Type
import javax.inject.Inject

@AndroidEntryPoint
class VideoPlayerActivity : AppCompatActivity() {
    private var player: SimpleExoPlayer? = null
    private lateinit var videoView: PlayerView


    private val videosAdapter = VideoPagingAdapter()

    private val viewModel: VideoPlayerViewModel by viewModels()

    @Inject
    lateinit var moshi: Moshi


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        setupRecycleView()

        //if(viewModel.curVideo == null)
        val videoData = intent.getSerializableExtra(VIDEO_KEY)!! as Video
        viewModel.videoData = videoData

        viewModel.headerData = videoData.run {
            HeaderData(title, createDate, playlist.name, description)
        }

        viewModel.videoId = videoData.media.id

        initButtons()

        videoView = findViewById(R.id.video_view)

        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            hideSystemUi()
            videoView.layoutParams.height = ConstraintLayout.LayoutParams.MATCH_PARENT
            btnRotate.setImageResource(R.drawable.ic_baseline_fullscreen_exit_24)
        } else {
            showSystemUi()
            videoView.layoutParams.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
            btnRotate.setImageResource(R.drawable.ic_baseline_fullscreen_24)
        }

        initChipGroup(videoData.keyword)

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

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            initPlayer()
            //playVideo(viewModel.curVideo.value)
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer();
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT < 24) {
            initPlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer()
        }
    }

    private fun initChipGroup(key: String) {
        val listMyData: Type = Types.newParameterizedType(
            MutableList::class.java,
            Keyword::class.java
        )
        val adapter: JsonAdapter<List<Keyword>> = moshi.adapter<List<Keyword>>(listMyData).lenient()

        val playlists = adapter.fromJson(key)

        val chip = getChip(getString(R.string.all)) {
            viewModel.revertFromAllPlaylist(viewModel.videoData!!.id).observe(this)
            {
                val text = if (it) "Rút lại video thành công" else "Lỗi khi rút video"
                Toast.makeText(this, text, Toast.LENGTH_LONG).show()
            }
        }
        update_video_chip_group.addView(chip)

        playlists?.forEach { playlist ->
            val chip = getChip(playlist.name) {
                viewModel.revertFromPlaylist(viewModel.videoData!!.id, playlist.privateKey)
                    .observe(this) {
                        val text = if (it) "Rút lại video thành công" else "Lỗi khi rút video"
                        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
                    }
            }
            update_video_chip_group.addView(chip)
        }
    }

    private fun getChip(name: String, clickListener: (View) -> Unit = {}) = Chip(this).apply {
        text = name
        chipIcon =
            ContextCompat.getDrawable(this@VideoPlayerActivity, R.drawable.ic_baseline_close_24)
        setOnClickListener(clickListener)
    }


    private fun initButtons() {
        btnRotate.setOnClickListener {
            val orientation = resources.configuration.orientation
            requestedOrientation =
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                } else {
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                }
        }
    }

    private fun hideSystemUi() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())

        } else {
            // hide status bar
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_IMMERSIVE or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }

    }

    private fun showSystemUi() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.show(WindowInsets.Type.statusBars())
        } else {
            // Show status bar
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.decorView.systemUiVisibility = SYSTEM_UI_FLAG_LAYOUT_STABLE
        }

    }

    private fun initPlayer() {
        player = SimpleExoPlayer.Builder(this).build()
        videoView.player = player
        player?.playWhenReady = true

    }

    private fun releasePlayer() {
        if (player != null) {
            viewModel.apply {
                playbackPosition = player?.currentPosition!!
                currentWindow = player?.currentWindowIndex!!
            }
            player?.release()
            player = null
        }
    }

    private fun setupRecycleView() {
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

    private fun playVideo(video: VideoDetail) {
        //uif (video == null) return

        //if(video.id != viewModel.curVideo?.id)
        viewModel.resetVideoParams()
       /* val path =
            "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"*/
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

    companion object {
        const val VIDEO_KEY = "videos_key"
    }
}