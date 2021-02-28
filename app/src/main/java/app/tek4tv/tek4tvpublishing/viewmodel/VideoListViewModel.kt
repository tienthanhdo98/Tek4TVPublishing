package app.tek4tv.tek4tvpublishing.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import app.tek4tv.tek4tvpublishing.model.PlaylistItem
import app.tek4tv.tek4tvpublishing.model.Video
import app.tek4tv.tek4tvpublishing.network.VideoPayload
import app.tek4tv.tek4tvpublishing.repositories.UserRepository
import app.tek4tv.tek4tvpublishing.repositories.VideoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class VideoListViewModel @ViewModelInject constructor(
    private val userRepository: UserRepository,
    private val videoRepository: VideoRepository
) : ViewModel() {



    var currentQuery = ""
    var currentPlaylist = PlaylistItem(0,"","")
    var allPlaylist  = listOf<PlaylistItem>()

    var videoPayload = VideoPayload()

    var pagingData = videoRepository.getSearchResult(videoPayload)
        .cachedIn(viewModelScope)

    fun setQuery(query: String = "",
                 playlistId: Long = 0,
                 privateKey: String = ""): Flow<PagingData<Video>> {
        pagingData = videoRepository.getSearchResult(videoPayload.apply {
            queryString = query
            this.playlistId = playlistId
            this.privateKey = privateKey
        })
        return pagingData
    }

    fun getUserPlaylists(): LiveData<List<PlaylistItem>> {
        val result = MutableLiveData<List<PlaylistItem>>()

        viewModelScope.launch {
            val response = userRepository.getUserPlaylist()

            if (response?.body() != null) {
                videoPayload.listID = response.body()!!.map { it.id!! }
                allPlaylist = response.body() ?: listOf()
                result.value = response.body()!!
            }
        }

        return result
    }
}