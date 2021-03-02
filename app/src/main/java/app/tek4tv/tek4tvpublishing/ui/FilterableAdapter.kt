package app.tek4tv.tek4tvpublishing.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import app.tek4tv.tek4tvpublishing.R
import java.util.*


/**
 * THREAD-UNSAFE
 * An adapter class with custom filter for search-autocomplete, since the default
 * ArrayAdapter cannot filter substring in the middle of the word
 * haven't check carefully but it seem kinda work for search auto-complete,
 * so don't use for any purpose except mention above
 */
class FilterableAdapter<T>(context: Context, objects: List<T?>) :
    ArrayAdapter<T?>(context, R.layout.search_item, ArrayList<T>() as List<T?>) {
    private var filter: CustomFilter? = null
    private var filteredResult: MutableList<T?>? = ArrayList()
    private val dataList: MutableList<T?> = ArrayList()
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val viewHolder: ViewHolder
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                .inflate(R.layout.search_item, parent, false)
            viewHolder = ViewHolder()
            viewHolder.textView = convertView!!.findViewById(R.id.text1)
            convertView.setTag(viewHolder)
        } else {
            viewHolder = convertView.tag as ViewHolder
        }
        viewHolder.textView!!.text = filteredResult!![position].toString()
        return convertView
    }

    override fun getCount(): Int {
        return filteredResult?.size ?: 0
    }

    override fun add(`object`: T?) {
        dataList.add(`object`)
    }

    override fun addAll(collection: Collection<T?>) {
        dataList.addAll(collection)
    }

    override fun addAll(vararg items: T?) {
        dataList.addAll(Arrays.asList(*items))
    }

    override fun remove(`object`: T?) {
        dataList.remove(`object`)
    }

    override fun clear() {
        dataList.clear()
    }

    override fun getItem(position: Int): T? {
        return filteredResult!![position]
    }

    override fun getPosition(item: T?): Int {
        return filteredResult!!.indexOf(item)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun resetData(collection: Collection<T?>) {
        dataList.clear()
        addAll(collection)
        filteredResult!!.clear()
    }

    override fun getFilter(): Filter {
        if (filter == null) filter = CustomFilter()
        return filter as CustomFilter
    }

    private class ViewHolder {
        var textView: TextView? = null
    }

    private inner class CustomFilter : Filter() {
        @Synchronized
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filterResults = FilterResults()
            filterResults.values = ArrayList<Any>()
            filterResults.count = 0
            if (constraint == null) return filterResults
            val filterString = constraint.toString().toLowerCase()
            val result: MutableList<T?> = ArrayList()
            if (filterString.length > 0) {
                for (i in dataList.indices) {
                    val item = dataList[i].toString()
                    if (item.toLowerCase().contains(filterString)) {
                        result.add(dataList[i])
                    }
                }
                filterResults.count = result.size
                filterResults.values = result
            } else {
                filterResults.values = 0
                filterResults.values = ArrayList<Any>()
            }
            return filterResults
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            filteredResult = results?.values as MutableList<T?>?
            notifyDataSetChanged()
        }
    }

    init {
        dataList.addAll(objects)
    }
}