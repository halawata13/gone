package net.halawata.gone.fragment

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.ContextCompat
import android.support.v4.content.Loader
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_list.*
import net.halawata.gone.R
import net.halawata.gone.entity.Article
import net.halawata.gone.entity.GnewsArticle
import net.halawata.gone.entity.SideMenuItem
import net.halawata.gone.service.*
import net.halawata.gone.view.ArticleListAdapter
import net.halawata.gone.view.DrawerListAdapter

class ListFragment : Fragment(), LoaderManager.LoaderCallbacks<AsyncNetworkTaskLoader.Response>, SwipeRefreshLayout.OnRefreshListener, ListFragmentDelegate {

    private var delegate: ListFragmentDelegate? = null
    private var readArticleService: ReadArticleService? = null
    private var drawerListAdapter: DrawerListAdapter? = null
    private var drawerToggle: ActionBarDrawerToggle? = null
    private var selectedItem: SideMenuItem? = null
    private var listener: OnListFragmentListener? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        delegate = context as? ListFragmentDelegate
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        val activity = activity ?: return view
        val data = ArrayList<GnewsArticle>()

        readArticleService = ReadArticleService(DatabaseHelper(activity))

        view.findViewById<SwipeRefreshLayout>(R.id.listSwipeRefreshLayout).setOnRefreshListener(this)

        val adapter = ArticleListAdapter(activity, data, R.layout.article_list_item)
        val listView = view.findViewById<ListView>(R.id.listView)
        listView.adapter = adapter

        listView.onItemClickListener = AdapterView.OnItemClickListener { _, articleListView, position, _ ->
            val titleTextView = articleListView.findViewById<TextView>(R.id.title)
            titleTextView.setTextColor(ContextCompat.getColor(activity, R.color.gray))
            delegate?.onClickArticle(adapter.data[position])
        }
        
        listView.onItemLongClickListener = AdapterView.OnItemLongClickListener { _, _, position, _ ->
            val dialog = ArticleOptionDialogFragment()
            val host = adapter.data[position].host
            val arguments = Bundle().apply {
                putString("host", host)
            }

            dialog.arguments = arguments
            dialog.setTargetFragment(this, 0)
            dialog.show(activity.supportFragmentManager, ArticleOptionDialogFragment::class.java.simpleName)

            true
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        LoaderManager.getInstance(this).getLoader<AsyncNetworkTaskLoader>(0)?.let {
            requestArticle()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        readArticleService = null
        drawerListAdapter = null
        drawerToggle = null
    }

    override fun onDetach() {
        super.onDetach()

        delegate = null
    }

    private fun setArticles(articles: ArrayList<GnewsArticle>) {
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

    private fun showLoadingView() {
        loadingView.visibility = View.VISIBLE
    }

    private fun hideLoadingView() {
        loadingView.visibility = View.GONE
    }

    /**
     * ローダー初期化
     */
    override fun onCreateLoader(id: Int, args: Bundle?): Loader<AsyncNetworkTaskLoader.Response> {
        val urlString = args?.getString("urlString", null) ?: ""

        return AsyncNetworkTaskLoader(activity!!, urlString, "GET")
    }

    /**
     * リクエスト完了時
     */
    override fun onLoadFinished(loader: Loader<AsyncNetworkTaskLoader.Response>, data: AsyncNetworkTaskLoader.Response?) {
        val activity = activity ?: return

        data?.content?.let { content ->
            GnewsService.parse(content)?.let { articles ->
                var parsedArticles = ReadArticleService(DatabaseHelper(activity)).checkReadArticle(articles, selectedItem!!.keyword)
                parsedArticles = MuteService(DatabaseHelper(activity)).filter(parsedArticles)
                parsedArticles = DateRangeService(DatabaseHelper(activity)).filter(parsedArticles)
                setArticles(parsedArticles)
            }
        }

        (listFragment as? ListFragment)?.hideLoadingView()
        listSwipeRefreshLayout.isRefreshing = false
        LoaderManager.getInstance(this).destroyLoader(0)
    }

    override fun onLoaderReset(loader: Loader<AsyncNetworkTaskLoader.Response>) {
        // noop
    }

    /**
     * 記事選択時
     */
    override fun onClickArticle(article: Article) {
        listener?.onArticleClicked(article)
    }

    /**
     * スワイプ更新時
     */
    override fun onRefresh() {
        requestArticle()
    }

    /**
     * 記事をリクエストする
     */
    fun requestArticle(item: SideMenuItem? = null) {
        selectedItem = item ?: selectedItem ?: return
        val urlString = SideMenuService.getUrlString(selectedItem!!)
        val bundle = Bundle().apply {
            putString("urlString", urlString)
        }

        showLoadingView()
        LoaderManager.getInstance(this).initLoader(0, bundle, this)
    }

    interface OnListFragmentListener {
        fun onArticleClicked(article: Article)
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
