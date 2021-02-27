package app.tek4tv.tek4tvpublishing.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.paging.insertHeaderItem
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import app.tek4tv.tek4tvpublishing.R
import app.tek4tv.tek4tvpublishing.model.UiModel
import app.tek4tv.tek4tvpublishing.model.Video
import app.tek4tv.tek4tvpublishing.viewmodel.VideoListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_video_list.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class VideoListActivity : AppCompatActivity()
{
    private val viewModel: VideoListViewModel by viewModels()
    private val videosAdapter = VideoPagingAdapter()
    lateinit var toolbar: Toolbar
    lateinit var searchView: SearchView
    lateinit var imgLogo: ImageView

    var collectVideoJob : Job? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_list)

        toolbar = findViewById(R.id.app_toolbar)
        setSupportActionBar(toolbar)

        val drawable = ContextCompat.getDrawable(applicationContext, R.drawable.ic_account_box_24)
        toolbar.overflowIcon = drawable
        title = ""


        searchView = findViewById(R.id.search_view)
        //imgUserAvatar = findViewById(R.id.img_user_avatar)
        imgLogo = findViewById(R.id.img_logo)

        searchView.setIconifiedByDefault(false)

        initSearch()

        setupRecycleView()
        registerObservers(viewModel.pagingData)


    }

    private fun initSearch()
    {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener
        {
            override fun onQueryTextSubmit(query: String?): Boolean
            {
                if (query != null)
                {
                    rv_videos.scrollToPosition(0)
                    val data = viewModel.setQuery(query)
                    registerObservers(data)
                }

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean
            {
                if (newText != null && newText == "")
                {
                    rv_videos.scrollToPosition(0)
                    val data = viewModel.setQuery("")
                    registerObservers(data)
                }
                return false
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {

        menuInflater.inflate(R.menu.video_list_menu, menu)

        //val searchItem = menu?.findItem(R.id.video_search)
        //val searchView = searchItem?.actionView as SearchView
        //searchView.setIconifiedByDefault(false)


        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        return when(item.itemId)
        {
            R.id.item_logout ->{
                val mainIntent = Intent(this, MainActivity::class.java)
                mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(mainIntent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupRecycleView()
    {
        rv_videos.adapter = videosAdapter
        rv_videos.layoutManager = LinearLayoutManager(this)
        videosAdapter.videoClickListener = {
            val intent = Intent(applicationContext, VideoPlayerActivity::class.java)
            intent.putExtra(VideoPlayerActivity.VIDEO_KEY, it)
            startActivity(intent)
        }
    }

    private fun registerObservers(data : Flow<PagingData<Video>>)
    {
        collectVideoJob?.cancel()
        collectVideoJob = lifecycleScope.launch {
            data.collectLatest {
                videosAdapter.submitData(it.map { video -> UiModel.VideoUiItem(video) })
            }
        }
    }
}