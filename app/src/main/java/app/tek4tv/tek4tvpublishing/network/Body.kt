package app.tek4tv.tek4tvpublishing.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Body
    (
    @Json(name = "IsSchedule")
    val isSchedule: Boolean = false,

    @Json(name = "ListID")
    val listID: List<Int> = listOf(
        7,
        2098,
        2049,
        2194,
        2089,
        2088,
        2087,
        2086,
        2085,
        2084,
        2083,
        2082,
        2079,
        2076,
        2093,
        2092,
        2091
    ),

    @Json(name = "PlaylistID")
    val playlistId: Long = 0,

    @Json(name = "Page")
    val page: Int = 0,

    @Json(name = "QueryString")
    val queryString: String = "",

    @Json(name = "Size")
    val size: Int = 18,

    @Json(name = "PrivateKey")
    val privateKey: String = ""
)