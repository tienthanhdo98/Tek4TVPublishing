package app.tek4tv.tek4tvpublishing.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MediaItem(
    @Json(name = "Type")
    val type: String,

    @Json(name = "Url")
    val url: String
)
