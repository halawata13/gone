package net.halawata.gone.activity

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.browser.customtabs.CustomTabsIntent
import androidx.appcompat.app.ActionBarDrawerToggle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.android.synthetic.main.activity_main.*
import net.halawata.gone.R
import net.halawata.gone.entity.Article
import net.halawata.gone.entity.SideMenuItem
import net.halawata.gone.fragment.ListFragment
import net.halawata.gone.fragment.ListFragmentDelegate
import net.halawata.gone.fragment.SideMenuFragment

class MainActivity : AppCompatActivity(), SideMenuFragment.OnSideMenuFragmentListener, ListFragmentDelegate {

    private lateinit var drawerToggle: ActionBarDrawerToggle

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult? ->
            if (result?.resultCode == Activity.RESULT_OK) {
                // 設定から戻ってきたらサイドメニューと記事を更新する
                (sideMenuFragment as? SideMenuFragment)?.updateSideMenu()
                (listFragment as? ListFragment)?.requestArticle()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        drawerToggle = object : ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {
            override fun onDrawerOpened(drawerView: View) {
                invalidateOptionsMenu()
            }

            override fun onDrawerClosed(drawerView: View) {
                invalidateOptionsMenu()
            }
        }

        drawerLayout.addDrawerListener(drawerToggle)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity, menu)
        return true
    }

    /**
     * オプションメニュー選択時
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // 設定画面へ
        if (item.itemId == R.id.configItem) {
            val intent = Intent(this, ConfigActivity::class.java)
            startForResult.launch(intent)
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

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        drawerToggle.onConfigurationChanged(newConfig)
    }

    /**
     * 記事選択時
     */
    override fun onClickArticle(article: Article) {
        val uri = Uri.parse(article.url) ?: return
        //val item = selectedItem ?: return
        //readArticleService.readArticle(article, item.keyword)

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
     * サイドメニュー選択時
     */
    override fun onSideMenuSelected(selectedItem: SideMenuItem) {
        supportActionBar?.title = selectedItem.keyword
        (listFragment as? ListFragment)?.requestArticle(selectedItem)
        drawerLayout?.closeDrawers()
    }

    companion object {
        object RequestCode {
            const val ConfigActivity = 0
        }
    }
}
