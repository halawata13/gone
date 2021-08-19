package net.halawata.gone.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import net.halawata.gone.R
import net.halawata.gone.entity.KeywordItem
import net.halawata.gone.entity.KeywordType
import net.halawata.gone.service.CustomKeywordsService
import net.halawata.gone.service.DatabaseHelper
import net.halawata.gone.service.ReadArticleService
import net.halawata.gone.view.DividerItemDecoration
import net.halawata.gone.view.KeywordRecyclerViewAdapter

class KeywordManagementFragment : Fragment(), View.OnClickListener {

    private var keywordsService: CustomKeywordsService? = null
    private var readArticleService: ReadArticleService? = null
    private var deletedItem = mutableListOf<String>()

    private lateinit var keywordRecyclerViewAdapter: KeywordRecyclerViewAdapter
    private lateinit var keywordRecyclerView: RecyclerView

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val helper = DatabaseHelper(context)
        keywordsService = CustomKeywordsService(helper)
        readArticleService = ReadArticleService(helper)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_keyword_management, container, false)
        val activity = activity ?: return view
        val keywordsService = keywordsService ?: return view

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
                dialog.show(childFragmentManager, KeywordClearFragment::class.java.simpleName)
            }
        }

        val linearLayoutManager =
            LinearLayoutManager(activity)

        keywordRecyclerView = view.findViewById(R.id.keywordRecyclerView)
        keywordRecyclerView.layoutManager = linearLayoutManager
        keywordRecyclerView.adapter = keywordRecyclerViewAdapter
        keywordRecyclerView.addItemDecoration(DividerItemDecoration(activity))

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT) {
            /**
             * 移動時
             */
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                val fromPosition = viewHolder.bindingAdapterPosition
                val toPosition = target.bindingAdapterPosition

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

        val keywordAddButton = view.findViewById<FloatingActionButton>(R.id.keywordAddButton)
        keywordAddButton.setOnClickListener(this)

        return view
    }

    override fun onStop() {
        super.onStop()

        // 保存
        try {
            keywordsService?.updateAll(keywordRecyclerViewAdapter.data)
            readArticleService?.deleteKeywords(deletedItem.distinct())

        } catch (ex: Exception) {
            showMessage()
        }
    }

    override fun onDetach() {
        super.onDetach()

        keywordsService = null
        readArticleService = null
    }

    /**
     * 追加ボタンタップ時
     */
    override fun onClick(view: View?) {
        val dialog = KeywordAdditionFragment()
        dialog.show(childFragmentManager, KeywordAdditionFragment::class.java.simpleName)
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
    private fun showMessage() {
        val activity = activity ?: return
        Toast.makeText(activity, getString(R.string.save_fail), Toast.LENGTH_LONG).show()
    }

    /**
     * キーワード追加ダイアログ
     */
    class KeywordAdditionFragment : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val fragment = parentFragment as? KeywordManagementFragment ?: return super.onCreateDialog(savedInstanceState)
            val context = context ?: return super.onCreateDialog(savedInstanceState)
            val builder = AlertDialog.Builder(context)
            val view = View.inflate(context, R.layout.fragment_keyword_addition, null)

            builder
                    .setView(view)
                    .setTitle(getString(R.string.add_item))
                    .setPositiveButton(getString(R.string.add)) { _, _ ->
                        val editText = view.findViewById<EditText>(R.id.keywordAdditionEditText)
                        val keyword = editText.text.toString()

                        fragment.onKeywordAddition(keyword)
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
            val fragment = parentFragment as? KeywordManagementFragment ?: return super.onCreateDialog(savedInstanceState)
            val context = context ?: return super.onCreateDialog(savedInstanceState)
            val builder = AlertDialog.Builder(context)

            builder
                    .setTitle(getString(R.string.confirm))
                    .setMessage(getString(R.string.confirm_delete))
                    .setPositiveButton(getString(R.string.delete)) { _, _ ->
                        arguments?.getInt("position")?.let {
                            fragment.onKeywordClear(it)
                        }
                    }
                    .setNegativeButton(getString(R.string.cancel), null)

            return builder.create()
        }
    }
}
