package net.halawata.gone

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_list.*
import net.halawata.gone.entity.Article
import net.halawata.gone.entity.GnewsArticle
import net.halawata.gone.view.ArticleListAdapter

class ListFragment : Fragment() {

    private var delegate: ListFragmentDelegate? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        val data = ArrayList<GnewsArticle>()

        val adapter = ArticleListAdapter(context!!, data, R.layout.article_list_item)
        val listView = view.findViewById<ListView>(R.id.listView)
        listView.adapter = adapter

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, articleListView, position, _ ->
            val titleTextView = articleListView.findViewById<TextView>(R.id.title)
            titleTextView.setTextColor(ContextCompat.getColor(context!!, R.color.gray))
            delegate?.onClickArticle(adapter.data[position])
        }

        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        delegate = context as? ListFragmentDelegate
    }

    override fun onDetach() {
        super.onDetach()

        delegate = null
    }

    fun <T: Article> setArticles(articles: ArrayList<T>) {
        val adapter = listView.adapter as? ArticleListAdapter<T>
        adapter?.refresh(articles)
    }

    fun showLoadingView() {
        loadingView.visibility = View.VISIBLE
    }

    fun hideLoadingView() {
        loadingView.visibility = View.GONE
    }
}
