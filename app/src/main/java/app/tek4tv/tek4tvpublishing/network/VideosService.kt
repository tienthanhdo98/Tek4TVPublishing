package app.tek4tv.tek4tvpublishing.network

import app.tek4tv.tek4tvpublishing.model.VideoDetail
import retrofit2.Response
import retrofit2.http.*
import retrofit2.http.Body

interface VideosService {
    @POST("https://ovp.tek4tv.vn/api/media/publish/home")
    suspend fun getVideo(
        @Body body: app.tek4tv.tek4tvpublishing.network.Body
    ): Response<VideosResponse>

    @GET("https://ovp.tek4tv.vn/api/publish/platform/media/{Id}")
    suspend fun getVideoDetail(
            @Path("Id") id : Long
    ): Response<VideoDetail>
}