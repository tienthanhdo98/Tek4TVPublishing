package app.tek4tv.tek4tvpublishing.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VideoPayload
    (
    @Json(name = "IsSchedule")
    var isSchedule: Boolean = false,

    @Json(name = "ListID")
    var listID: List<Long> = listOf(),

    @Json(name = "PlaylistID")
    var playlistId: Long = 0,

    @Json(name = "Page")
    var page: Int = 0,

    @Json(name = "QueryString")
    var queryString: String = "",

    @Json(name = "Size")
    val size: Int = 20,

    @Json(name = "PrivateKey")
    var privateKey: String = ""
)