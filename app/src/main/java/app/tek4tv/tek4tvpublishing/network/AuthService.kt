package app.tek4tv.tek4tvpublishing.network

import app.tek4tv.tek4tvpublishing.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthService {
    @Headers("Content-Type: application/json")
    @POST("https://mam.tek4tv.vn/api/token")
    suspend fun getToken(@Body body: Map<String, String>): Response<String>

    @Headers("Content-Type: application/json")
    @POST("https://mam.tek4tv.vn/iot/v1/app/login")
    suspend fun login(@Body body: Map<String, String>, @Header("Authorization") token: String): Response<User>
}