package app.tek4tv.tek4tvpublishing.repositories

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import app.tek4tv.tek4tvpublishing.model.PlaylistRevertPayload
import app.tek4tv.tek4tvpublishing.model.Video
import app.tek4tv.tek4tvpublishing.model.VideoDetail
import app.tek4tv.tek4tvpublishing.network.VideoPagingSource
import app.tek4tv.tek4tvpublishing.network.VideoPayload
import app.tek4tv.tek4tvpublishing.network.VideosResponse
import app.tek4tv.tek4tvpublishing.network.VideosService
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoRepository @Inject constructor(private val videosService: VideosService) {

    var currentVideoList = listOf<Video>()
    lateinit var currentPayload: VideoPayload

    suspend fun getVideos(videoPayload: VideoPayload): Response<VideosResponse>? {

        return try {
            val response = videosService.getVideo(videoPayload)

            if (response.isSuccessful) {
                currentVideoList = response.body()!!.result
            }
            response
        } catch (e: Exception) {
            Log.e("VideoRepo.getVideos", e.message ?: "")
            null
        }
    }

    suspend fun getVideoDetail(id: Long): Response<VideoDetail>? {
        return try {
            val response = videosService.getVideoDetail(id)
            response
        } catch (e: Exception) {
            Log.e("VideoRepo.getVideos", e.message ?: "")
            e.printStackTrace()
            null
        }
    }

    fun getSearchResult(videoPayload: VideoPayload?): Flow<PagingData<Video>> {
        if (videoPayload != null)
            currentPayload = videoPayload
        return Pager(
            config = PagingConfig(pageSize = 20)
        ) {
            if (videoPayload != null)
                VideoPagingSource(this, videoPayload)
            else
                VideoPagingSource(this, currentPayload)
        }
            .flow
    }

    suspend fun revertFromAllPlaylist(id: Long): Boolean {
        return videosService.revertFromAllPlaylist(id)
    }

    suspend fun revertFromPlaylist(revertPayload: PlaylistRevertPayload) =
        videosService.revertFromPlaylist(revertPayload)


}