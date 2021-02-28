package app.tek4tv.tek4tvpublishing.model

import com.squareup.moshi.Json

data class VideoDetail(
        @Json(name = "ID")
        val id: Long,

        @Json(name = "Name")
        val name: String,

        /*@Json(name = "ProjectCategoryID")
        val projectCategoryID: Long,

        @Json(name = "CateName")
        val cateName: String,*/

        @Json(name = "Description")
        val description: String,

       /* @Json(name = "ThumbNail")
        val thumbNail: String,

        @Json(name = "Banner")
        val banner: String,

        @Json(name = "Duration")
        val duration: String,

        @Json(name = "Metadata")
        val metadata: String,*/

        @Json(name = "Path")
        val path: String,

       /* @Json(name = "RemoteStorage")
        val remoteStorage: String,

        @Json(name = "StartProject")
        val startProject: String,

        @Json(name = "Member")
        val member: String,

        @Json(name = "State")
        val state: State*/
)

data class State (
        @Json(name = "ID")
        val id: Long,

        @Json(name = "Title")
        val title: String,

        @Json(name = "Color")
        val color: String,

        @Json(name = "PublishState")
        val publishState: Boolean,

        @Json(name = "TagPublish")
        val tagPublish: String
)

