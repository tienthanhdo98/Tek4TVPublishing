package app.tek4tv.tek4tvpublishing.network

import app.tek4tv.tek4tvpublishing.model.PlaylistItem
import app.tek4tv.tek4tvpublishing.model.User
import retrofit2.Response
import retrofit2.http.*
import retrofit2.http.Body

interface AuthService {
    @Headers("Content-Type: application/json")
    @POST("https://ovp.tek4tv.vn/api/token")
    suspend fun getToken(@Body body: Map<String, String>): Response<String>

    @Headers("Content-Type: application/json")
    @POST("https://ovp.tek4tv.vn/mprojects/ingest/share/login")
    suspend fun login(@Body body: Map<String, String>): Response<User>

    @GET("media/publish/playlist/user/{Id}")
    suspend fun getPlaylistListUser(@Path("Id") userId : String) : Response<List<PlaylistItem>>
}