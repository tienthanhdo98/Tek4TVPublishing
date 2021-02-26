package app.tek4tv.tek4tvpublishing.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.tek4tv.tek4tvpublishing.R
import app.tek4tv.tek4tvpublishing.model.Video
import com.bumptech.glide.Glide


class VideoAdapter() : RecyclerView.Adapter<VideoViewHolder>()
{
    var videos: List<Video> = listOf()
        set(value)
        {
            field = value
            notifyDataSetChanged()
        }

    var videoClickListener: (Video) -> Unit = {}


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder
    {
        return VideoViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int)
    {
        holder.bind(videos[position], videoClickListener)
    }

    override fun getItemCount(): Int = videos.size
}

class VideoViewHolder(private val root: View) : RecyclerView.ViewHolder(root)
{
    private val imgThumb = root.findViewById<ImageView>(R.id.img_video_thumb)
    private val txtTitle = root.findViewById<TextView>(R.id.txt_video_title)
    private val txtCreatedDate = root.findViewById<TextView>(R.id.txt_created_date)
    private val btnPublish = root.findViewById<Button>(R.id.btn_publish_revert)

    fun bind(video: Video, itemClickListener: (Video) -> Unit)
    {
        val thumbUrl = "https://vodovp.tek4tv.vn/${video.image
                .drop(28).dropLast(3).replace("\\\\\\\\", "\\")}"
        Glide.with(imgThumb.context)
                .load(thumbUrl)
                .into(imgThumb)

        btnPublish.text = if(video.isPublish) "Rút lại" else "Xuất bản"

        root.setOnClickListener {
            itemClickListener(video)
        }

        txtTitle.text = video.title
        txtCreatedDate.text = video.createDate
    }

    companion object
    {
        fun from(parent: ViewGroup): VideoViewHolder
        {
            val inflater = LayoutInflater.from(parent.context)
            val root = inflater.inflate(R.layout.video_item, parent, false)
            return VideoViewHolder(root)
        }
    }
}