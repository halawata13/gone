package net.halawata.gone

import android.app.Dialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_mute_management.*
import net.halawata.gone.service.DatabaseHelper
import net.halawata.gone.service.MuteService
import net.halawata.gone.view.DividerItemDecoration
import net.halawata.gone.view.MuteRecyclerViewAdapter

class MuteManagementActivity : AppCompatActivity() {

    lateinit var muteService: MuteService
    lateinit var muteRecyclerViewAdapter: MuteRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mute_management)

        supportActionBar?.title = getString(R.string.mute_management_activity_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_24)

        muteService = MuteService(DatabaseHelper(this))

        muteRecyclerViewAdapter = object : MuteRecyclerViewAdapter(muteService.getAll()) {
            /**
             * 削除ボタンタップ時
             */
            override fun onClearClicked(position: Int) {
                val dialog = MuteClearFragment()
                val arguments = Bundle().apply {
                    putInt("position", position)
                }

                dialog.arguments = arguments
                dialog.show(supportFragmentManager, MuteClearFragment::class.java.simpleName)
            }
        }

        val linearLayoutManager = LinearLayoutManager(this)

        muteRecyclerView.layoutManager = linearLayoutManager
        muteRecyclerView.adapter = muteRecyclerViewAdapter
        muteRecyclerView.addItemDecoration(DividerItemDecoration(this))
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            // 保存ボタンタップ時
            R.id.configSaveItem -> {
                try {
                    muteService.updateAll(muteRecyclerViewAdapter.data)

                    showMessage("保存しました")
                    finish()

                } catch (ex: Exception) {
                    showMessage("保存に失敗しました")
                }

                true
            }
            // 画面上の戻るボタンタップ時
            android.R.id.home -> {
                finish()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    /**
     * キーワード削除時
     */
    fun onKeywordClear(position: Int) {
        val adapter = muteRecyclerView.adapter as? MuteRecyclerViewAdapter ?: return
        adapter.notifyItemRemoved(position)
        adapter.data.removeAt(position)
    }

    /**
     * メッセージ表示
     */
    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    /**
     * ミュート削除ダイアログ
     */
    class MuteClearFragment : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val activity = activity as? MuteManagementActivity ?: return super.onCreateDialog(savedInstanceState)
            val context = context ?: return super.onCreateDialog(savedInstanceState)
            val builder = AlertDialog.Builder(context)

            builder
                    .setTitle(getString(R.string.confirm))
                    .setMessage(getString(R.string.confirm_delete))
                    .setPositiveButton(getString(R.string.delete)) { _, _ ->
                        arguments?.getInt("position")?.let {
                            activity.onKeywordClear(it)
                        }
                    }
                    .setNegativeButton(getString(R.string.cancel), null)

            return builder.create()
        }
    }
}
