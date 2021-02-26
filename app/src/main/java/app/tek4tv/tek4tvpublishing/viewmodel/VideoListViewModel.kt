package app.tek4tv.tek4tvpublishing.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import app.tek4tv.tek4tvpublishing.model.Video
import app.tek4tv.tek4tvpublishing.network.VideoPagingSource
import app.tek4tv.tek4tvpublishing.repositories.UserRepository
import app.tek4tv.tek4tvpublishing.repositories.VideoRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class VideoListViewModel @ViewModelInject constructor(
        private val userRepository: UserRepository,
        private val videoRepository: VideoRepository
) : ViewModel()
{

    var pagingData = videoRepository.getSearchResult("")
            .cachedIn(viewModelScope)


    fun setQuery(q : String) : Flow<PagingData<Video>>
    {
        pagingData = videoRepository.getSearchResult(q)
        return pagingData
    }

}