package app.tek4tv.tek4tvpublishing.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.insertHeaderItem
import androidx.paging.map
import app.tek4tv.tek4tvpublishing.model.HeaderData
import app.tek4tv.tek4tvpublishing.model.UiModel
import app.tek4tv.tek4tvpublishing.repositories.VideoRepository
import app.tek4tv.tek4tvpublishing.model.Video
import app.tek4tv.tek4tvpublishing.model.VideoDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

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
            if(detail!=null)
                curVideo.value = detail.body()!!
        }
    }

    val pagingData : Flow<PagingData<UiModel>> = videoRepository.getSearchResult("")
        .map<PagingData<Video>, PagingData<UiModel>> { pagingData-> pagingData.map { UiModel.VideoUiItem(it) } }
        .map {
            pagingData ->
            pagingData.insertHeaderItem(item = UiModel.HeaderItem(headerData))
        }

    /*fun getVideoPaging() : Flow<PagingData<UiModel>>
    {
        return videoRepository.getSearchResult("")
            .map<PagingData<Video>, PagingData<UiModel>> { pagingData -> pagingData.map { UiModel.VideoUiItem(it) } }
            .map<PagingData<UiModel>, PagingData<UiModel>> {
                it.insertHeaderItem(item = UiModel.HeaderItem(headerData))
            }
    }*/
}