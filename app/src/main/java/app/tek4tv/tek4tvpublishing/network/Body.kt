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
                2016,
                2026,
                2025,
                2055,
                2054,
                2056,
                2027,
                2024,
                2030,
                2031),

        @Json(name = "Page")
        val page: Int = 0,

        @Json(name = "QueryString")
        val queryString: String = "",

        @Json(name = "Size")
        val size: Int = 20
)