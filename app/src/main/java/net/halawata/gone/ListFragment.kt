package net.halawata.gone

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_list.*
import net.halawata.gone.entity.Article
import net.halawata.gone.entity.GnewsArticle
import net.halawata.gone.service.DatabaseHelper
import net.halawata.gone.service.MuteService
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
        
        listView.onItemLongClickListener = AdapterView.OnItemLongClickListener { _, _, position, _ ->
            (activity as? MainActivity)?.let {
                val dialog = ArticleOptionDialogFragment()
                val host = adapter.data[position].host
                val arguments = Bundle().apply {
                    putString("host", host)
                }

                dialog.arguments = arguments
                dialog.setTargetFragment(this, 0)
                dialog.show(it.supportFragmentManager, ArticleOptionDialogFragment::class.java.simpleName)
            }

            true
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

    fun setArticles(articles: ArrayList<GnewsArticle>) {
        val adapter = listView.adapter as? ArticleListAdapter
        adapter?.refresh(articles)

        // スクロール位置をトップにする
        if (articles.size > 0) {
            listView.setSelection(0)
        }
    }

    fun muteSite(host: String) {
        val context = context ?: return
        val muteService = MuteService(DatabaseHelper(context))
        val articles = (listView.adapter as? ArticleListAdapter)?.data ?: return
        muteService.add(host)

        val filteredArticles = muteService.filter(articles)
        setArticles(filteredArticles)
    }

    fun showLoadingView() {
        loadingView.visibility = View.VISIBLE
    }

    fun hideLoadingView() {
        loadingView.visibility = View.GONE
    }

    class ArticleOptionDialogFragment : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val activity = activity ?: return super.onCreateDialog(savedInstanceState)
            val builder = AlertDialog.Builder(activity)
            val host = arguments?.getString("host") ?: ""

            builder
                    .setTitle(host)
                    .setItems(arrayOf("このサイトをミュートする"), DialogInterface.OnClickListener { _, which ->
                        when (which) {
                            0 -> {
                                (targetFragment as? ListFragment)?.muteSite(host)
                            }
                            else -> return@OnClickListener
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), null)

            return builder.create()
        }
    }
}
