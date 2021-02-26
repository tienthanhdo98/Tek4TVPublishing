package app.tek4tv.tek4tvpublishing.network

import com.squareup.moshi.Json

class UserBody(
    @Json(name = "UserName")
    val username: String,
    @Json(name = "PassWord")
    val password: String) {
}