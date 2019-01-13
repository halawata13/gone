package net.halawata.gone

import android.app.Dialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_keyword_management.*
import net.halawata.gone.entity.KeywordItem
import net.halawata.gone.entity.KeywordType
import net.halawata.gone.service.DatabaseHelper
import net.halawata.gone.service.CustomKeywordsService
import net.halawata.gone.service.ReadArticleService
import net.halawata.gone.view.DividerItemDecoration
import net.halawata.gone.view.KeywordRecyclerViewAdapter

class KeywordManagementActivity : AppCompatActivity(), View.OnClickListener {

    private var deletedItem = mutableListOf<String>()

    private lateinit var readArticleService: ReadArticleService
    private lateinit var keywordsService: CustomKeywordsService
    private lateinit var keywordRecyclerViewAdapter: KeywordRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_keyword_management)

        supportActionBar?.title = getString(R.string.keyword_management_activity_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_24)

        val helper = DatabaseHelper(this)
        keywordsService = CustomKeywordsService(helper)
        readArticleService = ReadArticleService(helper)

        keywordRecyclerViewAdapter = object : KeywordRecyclerViewAdapter(keywordsService.getAll()) {
            /**
             * 削除ボタンタップ時
             */
            override fun onClearClicked(position: Int) {
                val dialog = KeywordClearFragment()
                val arguments = Bundle().apply {
                    putInt("position", position)
                }

                dialog.arguments = arguments
                dialog.show(supportFragmentManager, KeywordClearFragment::class.java.simpleName)
            }
        }

        val linearLayoutManager = LinearLayoutManager(this)

        keywordRecyclerView.layoutManager = linearLayoutManager
        keywordRecyclerView.adapter = keywordRecyclerViewAdapter
        keywordRecyclerView.addItemDecoration(DividerItemDecoration(this))

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT) {
            /**
             * 移動時
             */
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition

                val selectedItem = keywordRecyclerViewAdapter.data.removeAt(fromPosition)
                keywordRecyclerViewAdapter.data.add(toPosition, selectedItem)
                keywordRecyclerViewAdapter.notifyItemMoved(fromPosition, toPosition)

                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // noop
            }

            override fun getSwipeDirs(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                // スワイプしない
                return 0
            }
        })

        itemTouchHelper.attachToRecyclerView(keywordRecyclerView)

        keywordAddButton.setOnClickListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
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
     * 追加ボタンタップ時
     */
    override fun onClick(view: View?) {
        val dialog = KeywordAdditionFragment()
        dialog.show(supportFragmentManager, KeywordAdditionFragment::class.java.simpleName)
    }

    /**
     * 戻るボタンタップ時
     */
    override fun finish() {
        // 戻るときに保存する
        try {
            keywordsService.updateAll(keywordRecyclerViewAdapter.data)
            readArticleService.deleteKeywords(deletedItem.distinct())

        } catch (ex: Exception) {
            showMessage("保存に失敗しました")
        }

        super.finish()
    }

    /**
     * キーワード追加時
     */
    fun onKeywordAddition(keyword: String) {
        val adapter = keywordRecyclerView.adapter as? KeywordRecyclerViewAdapter ?: return
        adapter.data.add(KeywordItem(title = keyword, type = KeywordType.CUSTOM))
        adapter.notifyDataSetChanged()
    }

    /**
     * キーワード削除時
     */
    fun onKeywordClear(position: Int) {
        val adapter = keywordRecyclerView.adapter as? KeywordRecyclerViewAdapter ?: return
        val keyword = adapter.data[position]
        adapter.notifyItemRemoved(position)
        adapter.data.removeAt(position)

        deletedItem.add(keyword.title)
    }

    /**
     * メッセージ表示
     */
    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    /**
     * キーワード追加ダイアログ
     */
    class KeywordAdditionFragment : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val activity = activity as? KeywordManagementActivity ?: return super.onCreateDialog(savedInstanceState)
            val context = context ?: return super.onCreateDialog(savedInstanceState)
            val builder = AlertDialog.Builder(context)
            val view = View.inflate(context, R.layout.fragment_keyword_addition, null)

            builder
                    .setView(view)
                    .setTitle(getString(R.string.add_item))
                    .setPositiveButton(getString(R.string.add)) { _, _ ->
                        val editText = view.findViewById<EditText>(R.id.keywordAdditionEditText)
                        val keyword = editText.text.toString()

                        activity.onKeywordAddition(keyword)
                    }
                    .setNegativeButton(getString(R.string.cancel), null)

            return builder.create()
        }
    }

    /**
     * キーワード削除ダイアログ
     */
    class KeywordClearFragment : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val activity = activity as? KeywordManagementActivity ?: return super.onCreateDialog(savedInstanceState)
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
