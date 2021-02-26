package app.tek4tv.tek4tvpublishing.repositories

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import app.tek4tv.tek4tvpublishing.model.Video
import app.tek4tv.tek4tvpublishing.model.VideoDetail
import app.tek4tv.tek4tvpublishing.network.Body
import app.tek4tv.tek4tvpublishing.network.VideoPagingSource
import app.tek4tv.tek4tvpublishing.network.VideosResponse
import app.tek4tv.tek4tvpublishing.network.VideosService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoRepository @Inject constructor(private val videosService: VideosService)
{

    var allVideoList = mutableListOf<Video>()
    var currentVideoList = listOf<Video>()

    suspend fun getVideos(query: String = "", page: Int = 0): Response<VideosResponse>?
    {

        return try
        {
            val response = videosService.getVideo(Body(queryString = query, page = page))

            if (response.isSuccessful)
            {

                if (query == "")
                    allVideoList = response.body()!!.result as MutableList<Video>

                currentVideoList = response.body()!!.result
            }
            response
        } catch (e: Exception)
        {
            Log.e("VideoRepo.getVideos", e.message ?: "")
            null
        }
    }

    suspend fun getVideoDetail(id: Long): Response<VideoDetail>?
    {
        return try
        {
            val response = videosService.getVideoDetail(id)

            /*return if(response.isSuccessful) response
            else null*/
            response

        } catch (e: Exception)
        {
            Log.e("VideoRepo.getVideos", e.message ?: "")
            e.printStackTrace()
            null
        }
    }

    fun getSearchResult(query: String) = Pager(
            config = PagingConfig(pageSize = 20)
    ) {
        VideoPagingSource(this, query)
    }
            .flow
}