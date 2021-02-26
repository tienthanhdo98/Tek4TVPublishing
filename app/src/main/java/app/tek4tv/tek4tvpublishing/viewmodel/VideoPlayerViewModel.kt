package app.tek4tv.tek4tvpublishing.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.tek4tv.tek4tvpublishing.repositories.VideoRepository
import app.tek4tv.tek4tvpublishing.model.Video
import app.tek4tv.tek4tvpublishing.model.VideoDetail
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

    var videoList = videoRepository.allVideoList

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
}