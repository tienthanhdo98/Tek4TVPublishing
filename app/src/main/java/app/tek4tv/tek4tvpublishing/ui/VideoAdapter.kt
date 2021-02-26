package app.tek4tv.tek4tvpublishing.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.tek4tv.tek4tvpublishing.R
import app.tek4tv.tek4tvpublishing.model.MediaItem
import app.tek4tv.tek4tvpublishing.model.Video
import com.bumptech.glide.Glide
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.lang.reflect.Type
import java.text.SimpleDateFormat


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

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()
private var listMyData: Type = Types.newParameterizedType(
    MutableList::class.java,
    MediaItem::class.java
)
private var adapter: JsonAdapter<List<MediaItem>> = moshi.adapter<List<MediaItem>>(listMyData).lenient()

private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

class VideoViewHolder(private val root: View) : RecyclerView.ViewHolder(root)
{
    private val imgThumb = root.findViewById<ImageView>(R.id.img_video_thumb)
    private val txtTitle = root.findViewById<TextView>(R.id.txt_video_title)
    private val txtCreatedDate = root.findViewById<TextView>(R.id.txt_created_date)
    private val btnPublish = root.findViewById<Button>(R.id.btn_publish_revert)

    fun bind(video: Video, itemClickListener: (Video) -> Unit)
    {
        val thumbUrl =getThumbUrl(video.image)
        val dp = thumbUrl.lastIndexOf('.')
        val p1 = thumbUrl.subSequence(0, dp)
        val p2 = thumbUrl.subSequence(dp, thumbUrl.length)
        val finalUrl = "https://vodovp.tek4tv.vn/${p1}_320_180$p2"

        Glide.with(imgThumb.context)
                .load(finalUrl)
                .into(imgThumb)

        btnPublish.text = if(video.isPublish) "Rút lại" else "Xuất bản"

        root.setOnClickListener {
            itemClickListener(video)
        }

        txtTitle.text = video.title
        txtCreatedDate.text = getDateDiff(video.createDate, txtCreatedDate.context.resources)
    }

    private fun getThumbUrl(json: String) : String
    {
        val list = adapter.fromJson(json)
        return if(list !=null && list.isNotEmpty()) list[0].url
        else ""
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