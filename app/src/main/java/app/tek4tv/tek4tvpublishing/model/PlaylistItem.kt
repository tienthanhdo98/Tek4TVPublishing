package app.tek4tv.tek4tvpublishing.model

import com.squareup.moshi.Json

data class PlaylistItem(
    @Json(name = "ID")
    val id: Long?,

    @Json(name = "Name")
    val name: String?,

    @Json(name = "PrivateKey")
    val privateKey: String?
)
{
    override fun toString(): String {
        return name!!
    }
}
