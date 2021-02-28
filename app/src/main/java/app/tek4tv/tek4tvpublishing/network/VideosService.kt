package app.tek4tv.tek4tvpublishing.network

import app.tek4tv.tek4tvpublishing.model.PlaylistRevertPayload
import app.tek4tv.tek4tvpublishing.model.VideoDetail
import retrofit2.Response
import retrofit2.http.*
import retrofit2.http.Body

interface VideosService {
    @POST("media/publish/home")
    suspend fun getVideo(
        @Body videoPayload: app.tek4tv.tek4tvpublishing.network.VideoPayload
    ): Response<VideosResponse>

    @GET("publish/platform/media/{Id}")
    suspend fun getVideoDetail(
            @Path("Id") id : Long
    ): Response<VideoDetail>

    @GET("publish/platform/publish/appear/{Id}")
    suspend fun revertFromAllPlaylist(@Path("Id") id : Long) : Boolean

    @POST("media/publish/tag/update")
    suspend fun revertFromPlaylist(
        @Body revertPayload: PlaylistRevertPayload
    ) : Boolean


}