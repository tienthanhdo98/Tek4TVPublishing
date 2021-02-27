package app.tek4tv.tek4tvpublishing.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PlaylistRevertPayload(
    @Json(name = "ID")
    var id: Long,
    @Json(name = "Keyword")
    var key: String
)
