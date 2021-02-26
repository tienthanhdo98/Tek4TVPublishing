package app.tek4tv.tek4tvpublishing.network

import com.squareup.moshi.Json
import app.tek4tv.tek4tvpublishing.model.Video

class VideosResponse(
    @Json(name = "Total")
    val total : Int,
    @Json(name = "Result")
    val result : List<Video>
)
