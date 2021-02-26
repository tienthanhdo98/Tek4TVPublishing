package app.tek4tv.tek4tvpublishing.ui

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import app.tek4tv.tek4tvpublishing.model.Video

object UserComparator : DiffUtil.ItemCallback<Video>() {
    override fun areItemsTheSame(oldItem: Video, newItem: Video): Boolean {
        // Id is unique.
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Video, newItem: Video): Boolean {
        return oldItem == newItem
    }
}

class VideoPagingAdapter() : PagingDataAdapter<Video, VideoViewHolder>(UserComparator) {

    var videoClickListener: (Video) -> Unit = {}

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null)
            holder.bind(item, videoClickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        return VideoViewHolder.from(parent)
    }
}