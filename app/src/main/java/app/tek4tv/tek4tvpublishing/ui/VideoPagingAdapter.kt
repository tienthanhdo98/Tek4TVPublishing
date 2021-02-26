package app.tek4tv.tek4tvpublishing.ui

import android.annotation.SuppressLint
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import app.tek4tv.tek4tvpublishing.R
import app.tek4tv.tek4tvpublishing.model.HeaderData
import app.tek4tv.tek4tvpublishing.model.UiModel
import app.tek4tv.tek4tvpublishing.model.Video
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

object UserComparator : DiffUtil.ItemCallback<UiModel>() {
    override fun areItemsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
        // Id is unique.
        return (oldItem is UiModel.VideoUiItem && newItem is UiModel.VideoUiItem && oldItem.video.id == newItem.video.id)
                || (oldItem is UiModel.HeaderItem && newItem is UiModel.HeaderItem && oldItem.data == newItem.data)
    }

    override fun areContentsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
        return oldItem == newItem
    }
}

class VideoPagingAdapter() : PagingDataAdapter<UiModel, RecyclerView.ViewHolder>(UserComparator) {

    var videoClickListener: (Video) -> Unit = {}

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        item.let {
            when(item)
            {
                is UiModel.VideoUiItem -> (holder as VideoViewHolder).bind(item.video, videoClickListener)
                is UiModel.HeaderItem -> (holder as HeaderViewHolder).bind(item.data)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is UiModel.VideoUiItem -> R.layout.video_item
            is UiModel.HeaderItem -> R.layout.header_item
            else -> throw UnsupportedOperationException("Unknown view")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.video_item -> VideoViewHolder.from(parent)
            R.layout.header_item -> HeaderViewHolder.from(parent)
            else -> throw UnsupportedOperationException("Unknown view")
        }
        //return VideoViewHolder.from(parent)
    }
}

private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")//"dd/MM/yyyy HH:mm:ss"

class HeaderViewHolder(private val root: View) : RecyclerView.ViewHolder(root) {

    private val txtTitle = root.findViewById<TextView>(R.id.txt_title)
    private val txtCreatedDate = root.findViewById<TextView>(R.id.txt_create_date)
    private val txtDuration = root.findViewById<TextView>(R.id.txt_duration_from_create)
    private val txtDescription = root.findViewById<TextView>(R.id.txt_description)
    private val txtTag = root.findViewById<TextView>(R.id.txt_tag)


    @SuppressLint("SetTextI18n")
    fun bind(data: HeaderData) {
        data.apply {
            txtTitle.text = title
            txtDescription.text = description
            val date = Calendar.getInstance().apply {
                time = dateFormat.parse(createDate)
            }
            txtCreatedDate.text =
                "Được tạo vào ${date.get(Calendar.DAY_OF_MONTH)}" +
                        "-${date.get(Calendar.MONTH)}-${date.get(Calendar.YEAR)}"
            txtDuration.text = getDateDiff(createDate, txtCreatedDate.context.resources)
            txtTag.text = "Tags: $tag"
        }
    }


    companion object {
        fun from(parent: ViewGroup): HeaderViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val root = inflater.inflate(R.layout.header_item, parent, false)
            return HeaderViewHolder(root)
        }
    }
}

fun getDateDiff(date1: String, resource: Resources): String {
    val date = dateFormat.parse(date1)
    val diff = abs(Date().time - date!!.time)
    val diffMinutes: Long = diff / (60 * 1000)
    val diffHours: Long = diff / (60 * 60 * 1000)
    val diffDays: Long = diff / (60 * 60 * 1000 * 24)
    val diffMonths = (diff / (60 * 60 * 1000 * 24 * 30.41666666))
    val diffYears: Long = diff / (60.toLong() * 60 * 1000 * 24 * 365)


    return when {
        diffYears >= 1 -> resource.getString(R.string.years_before, diffYears.toInt())
        diffMonths >= 1 -> resource.getString(R.string.months_before, diffMonths.toInt())
        diffDays >= 1 -> resource.getString(R.string.days_before, diffDays.toInt())
        diffHours >= 1 -> resource.getString(R.string.hours_before, diffHours.toInt())
        diffMinutes >= 1 -> resource.getString(R.string.minutes_before, diffMinutes.toInt())
        else -> resource.getString(R.string.minutes_before, 1)
    }
}