package app.tek4tv.tek4tvpublishing.network

import androidx.paging.PagingSource
import androidx.paging.PagingState
import app.tek4tv.tek4tvpublishing.model.Video
import app.tek4tv.tek4tvpublishing.repositories.VideoRepository

class VideoPagingSource(
    private val videoRepo: VideoRepository,
    private val videoPayload: VideoPayload
) : PagingSource<Int, Video>() {


    override fun getRefreshKey(state: PagingState<Int, Video>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Video> {
        return try {
            val page = params.key ?: 0
            val res = videoRepo.getVideos(videoPayload)
            if (res != null && res.isSuccessful) {
                LoadResult.Page(
                    data = res.body()!!.result,
                    prevKey = if (page == 0) null else page - 1,
                    nextKey = if (res.body()!!.result.isEmpty()) null else page + 1
                )
            } else LoadResult.Error(Exception("Unnable load page: $page"))
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}