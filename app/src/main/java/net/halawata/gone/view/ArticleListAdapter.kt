package net.halawata.gone.view

import android.app.Activity
import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import net.halawata.gone.entity.Article
import net.halawata.gone.R
import kotlin.collections.ArrayList

class ArticleListAdapter<T: Article>(val context: Context, var data: ArrayList<T>, val resource: Int): BaseAdapter() {

    override fun getCount(): Int = data.size

    override fun getItem(position: Int): Any = data[position]

    override fun getItemId(position: Int): Long = data[position].id

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val activity = context as Activity
        val item = getItem(position) as Article

        val view = convertView ?: activity.layoutInflater.inflate(resource, null)

        (view.findViewById(R.id.pubDate) as TextView).text = item.pubDate
        (view.findViewById(R.id.title) as TextView).text = item.title
        (view.findViewById(R.id.url) as TextView).text = item.url

        val textColorRes = if (item.isRead) R.color.gray else R.color.text_color_default

        (view.findViewById(R.id.title) as TextView).setTextColor(ContextCompat.getColor(context, textColorRes))

        return view
    }

    fun refresh(newData: ArrayList<T>) {
        data.clear()
        data = newData
        notifyDataSetChanged()
    }
}
