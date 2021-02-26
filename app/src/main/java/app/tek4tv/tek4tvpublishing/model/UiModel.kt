package app.tek4tv.tek4tvpublishing.model

sealed class UiModel{
    data class VideoUiItem(val video: Video) : UiModel()
    data class HeaderItem(val data: HeaderData) : UiModel()
}

data class HeaderData(
    val title : String,
    val createDate : String,
    val tag : String,
    val description : String
)