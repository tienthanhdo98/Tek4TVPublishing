package app.tek4tv.tek4tvpublishing.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import app.tek4tv.tek4tvpublishing.R
import app.tek4tv.tek4tvpublishing.model.PlaylistItem
import app.tek4tv.tek4tvpublishing.model.UiModel
import app.tek4tv.tek4tvpublishing.model.Video
import app.tek4tv.tek4tvpublishing.viewmodel.VideoListViewModel
import com.google.android.material.chip.Chip
import com.toptoche.searchablespinnerlibrary.SearchableSpinner
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_video_list.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class VideoListActivity : AppCompatActivity() {
    private val viewModel: VideoListViewModel by viewModels()
    private val videosAdapter = VideoPagingAdapter()
    lateinit var toolbar: Toolbar
    lateinit var searchView: SearchView
    lateinit var playlistSearch: SearchableSpinner
    lateinit var imgLogo: ImageView
    lateinit var swipeLayout: SwipeRefreshLayout
    lateinit var playlistSearchAutoComplete: SearchView.SearchAutoComplete
    lateinit var playlistSearchAdapter: FilterableAdapter<PlaylistItem>
    var currentChip: Chip? = null
    lateinit var allChip: Chip

    var collectVideoJob: Job? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_list)

        swipeLayout = findViewById(R.id.swipe_layout)
        searchView = findViewById(R.id.search_view)
        playlistSearch = findViewById(R.id.playlist_search)
        imgLogo = findViewById(R.id.img_logo)
        allChip = findViewById(R.id.all_chip)

        swipeLayout.setDistanceToTriggerSync(250)
        swipeLayout.setOnRefreshListener {
            viewModel.apply {
                setQuery(currentQuery, currentPlaylist.id!!, currentPlaylist.privateKey!!)
            }
            registerObservers(viewModel.pagingData)
        }

        toolbar = findViewById(R.id.app_toolbar)
        setSupportActionBar(toolbar)
        val drawable = ContextCompat.getDrawable(applicationContext, R.drawable.ic_account_box_24)
        toolbar.overflowIcon = drawable
        title = ""

        //playlistSearch.setIconifiedByDefault(false)


        viewModel.getUserPlaylists().observe(this)
        {
            initChip(it)
            initPlaylistSearchView(it)
        }


        initSearch()
        setupRecycleView()
    }

    private fun initChip(list: List<PlaylistItem>) {
        /*allChip = getChip(PlaylistItem(0, getString(R.string.all), ""))
        {
            viewModel.currentPlaylist = PlaylistItem(0, getString(R.string.all), "")
            rv_videos.scrollToPosition(0)
            viewModel.setQuery(viewModel.currentQuery, 0, "")
            registerObservers(viewModel.pagingData)
        }
        allChip.isChecked = true*/
        //currentChip = allChip
        /*playlist_chip_group.addView(allChip)
        list.forEach { item ->
            playlist_chip_group.addView(getChip(item) {
                viewModel.currentPlaylist = item
                rv_videos.scrollToPosition(0)
                viewModel.setQuery(viewModel.currentQuery, item.id!!, item.privateKey!!)
                registerObservers(viewModel.pagingData)
            })
        }*/

        allChip.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {

                if(playlistSearch.selectedItemPosition == 0)
                {
                    allChip.isClickable = false
                    return@setOnCheckedChangeListener
                }

                viewModel.currentPlaylist = PlaylistItem(0, getString(R.string.all), "")
                rv_videos.scrollToPosition(0)
                viewModel.setQuery(viewModel.currentQuery, 0, "")
                registerObservers(viewModel.pagingData)

                allChip.isClickable = false

                playlistSearch.setSelection(0)
            }
        }
        allChip.isChecked = true
    }

    private fun getChip(playlistItem: PlaylistItem, checkedListener: () -> Unit) =
        Chip(this).apply {
            text = playlistItem.name
            isCheckable = true

            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    currentChip?.isChecked = false
                    currentChip = this

                    //disable uncheck all category chip
                    if (currentChip == allChip)
                        allChip.isClickable = false

                    checkedListener.invoke()
                } else {
                    //if a chip different from all category chip is uncheck, check all chip to get all video
                    if (!(this === currentChip)) {
                        rv_videos.scrollToPosition(0)
                        currentChip = allChip
                        allChip.isChecked = true
                    }
                    //if all category chip is uncheck, it can be clicked again
                    else
                        allChip.isClickable = true
                }
            }
        }

    private fun initSearch() {
        searchView.setIconifiedByDefault(false)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    rv_videos.scrollToPosition(0)
                    viewModel.currentQuery = query
                    val data = viewModel.run {
                        viewModel.setQuery(
                            query,
                            currentPlaylist.id!!,
                            currentPlaylist.privateKey!!
                        )
                    }
                    registerObservers(data)
                }

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null && newText == "") {
                    rv_videos.scrollToPosition(0)
                    viewModel.currentQuery = ""
                    val data = viewModel.run {
                        viewModel.setQuery("", currentPlaylist.id!!, currentPlaylist.privateKey!!)
                    }
                    registerObservers(data)
                }
                return false
            }
        })
    }

    private fun initPlaylistSearchView(list: List<PlaylistItem>) {
        /*playlistSearchAutoComplete = playlistSearch.findViewById(androidx.appcompat.R.id.search_src_text)
        val sm = getSystemService(SEARCH_SERVICE) as SearchManager
        playlistSearchAdapter = FilterableAdapter(this, list)

        //playlistSearch.setSearchableInfo(sm.getSearchableInfo(componentName))
        playlistSearchAutoComplete.setAdapter(playlistSearchAdapter)

        playlistSearchAutoComplete.setOnItemClickListener { parent, view, position, id ->
            val playlist = playlistSearchAdapter.getItem(position)!!

            viewModel.currentPlaylist = playlist
            rv_videos.scrollToPosition(0)
            viewModel.setQuery(viewModel.currentQuery, playlist.id!!, playlist.privateKey!!)
            registerObservers(viewModel.pagingData)
        }*/
        val items = mutableListOf(PlaylistItem(0, getString(R.string.all), ""))
        items.addAll(list)
        val a = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, items)
        playlistSearch.setAdapter(a)
        playlistSearch.setTitle(getString(R.string.select_playlist))
        playlistSearch.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val playlist = a.getItem(position)!!

                if(playlist.name == getString(R.string.all) && allChip.isChecked)
                    return

                viewModel.currentPlaylist = playlist
                rv_videos.scrollToPosition(0)
                viewModel.setQuery(viewModel.currentQuery, playlist.id!!, playlist.privateKey!!)
                registerObservers(viewModel.pagingData)

                if (playlist.name != getString(R.string.all)) {
                    allChip.isChecked = false
                    allChip.isClickable = true
                }
                else
                {

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                /*allChip.isChecked = false
                allChip.isClickable = true*/
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.video_list_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_logout -> {
                val mainIntent = Intent(this, MainActivity::class.java)
                mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(mainIntent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupRecycleView() {
        rv_videos.adapter = videosAdapter
        rv_videos.layoutManager = LinearLayoutManager(this)
        videosAdapter.videoClickListener = {
            val intent = Intent(applicationContext, VideoPlayerActivity::class.java)
            intent.putExtra(VideoPlayerActivity.VIDEO_KEY, it)
            startActivity(intent)
        }
    }

    private fun registerObservers(data: Flow<PagingData<Video>>) {
        collectVideoJob?.cancel()
        collectVideoJob = lifecycleScope.launch {
            data.collectLatest {
                swipeLayout.isRefreshing = false
                videosAdapter.submitData(it.map { video -> UiModel.VideoUiItem(video) })
            }
        }
    }
}