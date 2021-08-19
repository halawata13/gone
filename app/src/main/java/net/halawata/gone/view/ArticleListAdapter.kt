package net.halawata.gone.view

import android.app.Activity
import android.content.Context
import androidx.core.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import net.halawata.gone.R
import net.halawata.gone.entity.GnewsArticle
import kotlin.collections.ArrayList

class ArticleListAdapter(val context: Context, var data: ArrayList<GnewsArticle>, val resource: Int) : BaseAdapter() {

    override fun getCount(): Int = data.size

    override fun getItem(position: Int): Any = data[position]

    override fun getItemId(position: Int): Long = data[position].id

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val activity = context as Activity
        val article = getItem(position) as GnewsArticle
        val view = convertView ?: activity.layoutInflater.inflate(resource, null)

        (view.findViewById(R.id.pubDate) as TextView).text = article.pubDate
        (view.findViewById(R.id.title) as TextView).text = article.title
        (view.findViewById(R.id.url) as TextView).text = article.source

        val textColorRes = if (article.isRead) R.color.gray else R.color.text_color_default
        (view.findViewById(R.id.title) as TextView).setTextColor(ContextCompat.getColor(context, textColorRes))

        return view
    }

    /**
     * 記事を差し替える
     */
    fun refresh(newData: ArrayList<GnewsArticle>) {
        data.clear()
        data = newData
        notifyDataSetChanged()
    }
}
