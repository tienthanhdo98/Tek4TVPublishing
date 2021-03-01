package app.tek4tv.tek4tvpublishing.model

import com.squareup.moshi.Json
import java.io.Serializable

/*data class Video(
    @Json(name = "ID")
    val id: Int,
    @Json(name = "ThumbNail")
    val thumbUrl: String,
    @Json(name = "CreateDate")
    val createDate: String,
    @Json(name = "Title")
    val title: String,
    @Json(name = "Description")
    val description: String,
    @Json(name = "IsSchedule")
    val isSchedule: Boolean,
    @Json(name = "Schedule")
    val schedule: String,
    @Json(name = "Status")
    val status: String,
    @Json(name = "Media")
    val media: Media,
    @Json(name = "Playlist")
    val playList: PlayList,
    @Json(name = "Path")
    var path: String
) : Serializable

data class Media(
    @Json(name = "ID")
    val id: Int,
    @Json(name = "Name")
    val name: String
) : Serializable

data class PlayList(
    @Json(name = "Name")
    val name: String,
    @Json(name = "ID")
    val id: Int,
    @Json(name = "Icon")
    val iconUrl: String,
    @Json(name = "Color")
    val color: String
) : Serializable*/

data class Video (
        /*@Json(name = "Episode")
        val episode: String,*/

        @Json(name = "ID")
        val id: Long,

        @Json(name = "Image")
        val image: String,

        @Json(name = "CreateDate")
        val createDate: String,

        @Json(name = "Title")
        val title: String,

        @Json(name = "Description")
        val description: String,

        @Json(name = "IsSchedule")
        val isSchedule: Boolean,

        @Json(name = "Schedule")
        val schedule: String,

        /*@Json(name = "Status")
        val status: String,

        @Json(name = "IsPublish")
        val isPublish: Boolean,*/

        @Json(name = "Media")
        val media: Media,

        @Json(name = "Playlist")
        val playlist: Playlist,
        @Json(name = "Keyword")
        val keyword: String
) : Serializable

data class Media (
        @Json(name = "ID")
        val id: Long,

        @Json(name = "Name")
        val name: String
): Serializable

data class Playlist (
        @Json(name = "Name")
        val name: String,

        @Json(name = "ID")
        val id: Long,

        @Json(name = "Icon")
        val icon: String,

        @Json(name = "Color")
        val color: String
): Serializable
