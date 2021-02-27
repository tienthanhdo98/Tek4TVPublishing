package app.tek4tv.tek4tvpublishing.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.insertHeaderItem
import androidx.paging.map
import app.tek4tv.tek4tvpublishing.model.*
import app.tek4tv.tek4tvpublishing.repositories.VideoRepository
import app.tek4tv.tek4tvpublishing.network.Keyword
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.security.PrivateKey

class VideoPlayerViewModel @ViewModelInject constructor(
    private val videoRepository: VideoRepository
) : ViewModel() {
    var currentWindow = 0
    var playbackPosition: Long = 0
    var curVideo = MutableLiveData<VideoDetail>()
    var videoId : Long = 0L
    set(value)
    {
        field = value
        getVideoDetail(value)
    }
    var videoData : Video? = null

    var headerData = HeaderData("","","","")

    fun resetVideoParams()
    {
        currentWindow = 0
        playbackPosition = 0
    }

    fun getVideoDetail(id : Long)
    {
        viewModelScope.launch {
            val detail = videoRepository.getVideoDetail(id)
            if(detail?.body() != null)
                curVideo.value = detail.body()
        }
    }

    val pagingData : Flow<PagingData<UiModel>> = videoRepository.getSearchResult("")
        .map<PagingData<Video>, PagingData<UiModel>> { pagingData-> pagingData.map { UiModel.VideoUiItem(it) } }
        .map {
            pagingData ->
            pagingData.insertHeaderItem(item = UiModel.HeaderItem(headerData))
        }

    fun revertFromAllPlaylist(id : Long) : LiveData<Boolean>
    {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            result.value = videoRepository.revertFromAllPlaylist(id)
        }
        return result
    }

    fun revertFromPlaylist(id : Long, privateKey: String) : LiveData<Boolean>
    {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            val payload = PlaylistRevertPayload(id , privateKey)
            result.value = videoRepository.revertFromPlaylist(payload)
        }
        return result
    }
}