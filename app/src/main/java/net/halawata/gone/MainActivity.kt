package net.halawata.gone

import android.app.Dialog
import android.app.PendingIntent
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.app.DialogFragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.ContextCompat
import android.support.v4.content.Loader
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ExpandableListView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_list.*
import net.halawata.gone.entity.Article
import net.halawata.gone.entity.SideMenuItem
import net.halawata.gone.service.*
import net.halawata.gone.view.DrawerListAdapter

class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<AsyncNetworkTaskLoader.Response>, SwipeRefreshLayout.OnRefreshListener, ExpandableListView.OnChildClickListener, ExpandableListView.OnGroupClickListener, ListFragmentDelegate {

    private val loaderId = 1

    private var selectedItem: SideMenuItem? = null

    private lateinit var drawerListAdapter: DrawerListAdapter
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var readArticleService: ReadArticleService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        readArticleService = ReadArticleService(DatabaseHelper(this))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        drawerListAdapter = DrawerListAdapter(this, arrayListOf(), arrayListOf(arrayListOf()), R.layout.drawer_section_item, R.layout.drawer_list_item)
        drawerListView.setAdapter(drawerListAdapter)

        drawerToggle = object : ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {
            override fun onDrawerOpened(drawerView: View) {
                invalidateOptionsMenu()
            }

            override fun onDrawerClosed(drawerView: View) {
                invalidateOptionsMenu()
            }
        }

        drawerLayout.addDrawerListener(drawerToggle)

        drawerListView.setOnChildClickListener(this)
        drawerListView.setOnGroupClickListener(this)

        listSwipeRefreshLayout.setOnRefreshListener(this)

        LoaderManager.getInstance(this).getLoader<AsyncNetworkTaskLoader>(loaderId)?.let {
            requestArticle()
        }

        updateSideMenu()
        requestArticle()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RequestCode.ConfigActivity -> {
                // 設定から戻ってきたらサイドメニューと記事を更新する
                updateSideMenu()
                requestArticle()
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity, menu)
        return true
    }

    /**
     * オプションメニュー選択時
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.configItem) {
            val intent = Intent(this, ConfigActivity::class.java)
            startActivityForResult(intent, RequestCode.ConfigActivity)
        }

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        drawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        drawerToggle.onConfigurationChanged(newConfig)
    }

    /**
     * ローダー初期化
     */
    override fun onCreateLoader(id: Int, args: Bundle?): Loader<AsyncNetworkTaskLoader.Response> {
        val urlString = args?.getString("urlString", null) ?: ""

        return AsyncNetworkTaskLoader(application, urlString, "GET")
    }

    /**
     * リクエスト完了時
     */
    override fun onLoadFinished(loader: Loader<AsyncNetworkTaskLoader.Response>, data: AsyncNetworkTaskLoader.Response?) {
        data?.let {
            it.content?.let { content ->
                GnewsService.parse(content)?.let { articles ->
                    var parsedArticles = readArticleService.checkReadArticle(articles, selectedItem!!.keyword)
                    parsedArticles = MuteService(DatabaseHelper(this)).filter(parsedArticles)
                    parsedArticles = DateRangeService(DatabaseHelper(this)).filter(parsedArticles)
                    (listFragment as? ListFragment)?.setArticles(parsedArticles)
                }
            }
        }

        (listFragment as? ListFragment)?.hideLoadingView()
        listSwipeRefreshLayout.isRefreshing = false
        LoaderManager.getInstance(this).destroyLoader(loaderId)
    }

    override fun onLoaderReset(loader: Loader<AsyncNetworkTaskLoader.Response>) {
        // noop
    }

    /**
     * 記事選択時
     */
    override fun onClickArticle(article: Article) {
        val uri = Uri.parse(article.url) ?: return
        val item = selectedItem ?: return

        readArticleService.readArticle(article, item.keyword)

        val intent = Intent(Intent.ACTION_SEND)
                .setType("text/plain")
                .putExtra(Intent.EXTRA_TEXT, uri.toString())

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        val icon = BitmapFactory.decodeResource(resources, R.drawable.baseline_share_white_24)

        val customTabsIntent = CustomTabsIntent.Builder()
                .setShowTitle(true)
                .setStartAnimations(this, android.R.anim.fade_in, android.R.anim.fade_out)
                .setExitAnimations(this, android.R.anim.fade_in, android.R.anim.fade_out)
                .setCloseButtonIcon(BitmapFactory.decodeResource(this.resources, R.drawable.baseline_arrow_back_white_24))
                .setActionButton(icon, "共有", pendingIntent)
                .build()

        customTabsIntent.launchUrl(this, uri)
    }

    /**
     * スワイプ更新時
     */
    override fun onRefresh() {
        requestArticle()
    }

    /**
     * サイドメニュー選択時
     */
    override fun onChildClick(parent: ExpandableListView?, v: View?, groupPosition: Int, childPosition: Int, id: Long): Boolean {
        selectedItem = drawerListAdapter.data[groupPosition][childPosition]

        requestArticle()
        drawerLayout.closeDrawer(drawerListView)

        return true
    }

    /**
     * サイドメニューセクション選択時
     */
    override fun onGroupClick(parent: ExpandableListView?, v: View?, groupPosition: Int, id: Long): Boolean {
        // 常に開いた状態にするためタップは無効化
        return true
    }

    /**
     * サイドメニューを構築する
     */
    private fun updateSideMenu() {
        val sideMenu = SideMenuService(this).getSideMenu()
        selectedItem = sideMenu.items[0].firstOrNull()
        drawerListAdapter.sectionData = sideMenu.sections
        drawerListAdapter.data = sideMenu.items
        drawerListAdapter.notifyDataSetChanged()

        // 最初から開いた状態に
        for (i in 0 until drawerListAdapter.groupCount) {
            drawerListView.expandGroup(i, false)
        }
    }

    /**
     * 記事をリクエストする
     */
    private fun requestArticle() {
        val bundle = selectedItem?.let {
            supportActionBar?.title = it.keyword

            val urlString = SideMenuService.getUrlString(it)
            Bundle().apply {
                putString("urlString", urlString)
            }
        }

        (listFragment as? ListFragment)?.showLoadingView()
        LoaderManager.getInstance(this).initLoader(loaderId, bundle, this)
    }

    companion object {
        object RequestCode {
            const val ConfigActivity = 0
            const val KeywordManagementActivity = 1
        }
    }
}
