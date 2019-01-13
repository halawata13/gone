package net.halawata.gone.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import net.halawata.gone.R
import net.halawata.gone.service.DatabaseHelper
import net.halawata.gone.service.MuteService
import net.halawata.gone.view.DividerItemDecoration
import net.halawata.gone.view.MuteRecyclerViewAdapter

class MuteManagementFragment : Fragment() {

    private var muteService: MuteService? = null

    private lateinit var muteRecyclerViewAdapter: MuteRecyclerViewAdapter
    private lateinit var muteRecyclerView: RecyclerView

    override fun onAttach(context: Context) {
        super.onAttach(context)

        muteService = MuteService(DatabaseHelper(context))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_mute_management, container, false)
        val activity = activity ?: return view
        val muteService = muteService ?: return view

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
                dialog.show(childFragmentManager, MuteClearFragment::class.java.simpleName)
            }
        }

        val linearLayoutManager = LinearLayoutManager(activity)

        muteRecyclerView = view.findViewById(R.id.muteRecyclerView)
        muteRecyclerView.layoutManager = linearLayoutManager
        muteRecyclerView.adapter = muteRecyclerViewAdapter
        muteRecyclerView.addItemDecoration(DividerItemDecoration(activity))

        return view
    }

    override fun onStop() {
        super.onStop()

        try {
            muteService?.updateAll(muteRecyclerViewAdapter.data)

        } catch (ex: Exception) {
            showMessage("保存に失敗しました")
        }
    }

    override fun onDetach() {
        super.onDetach()

        muteService = null
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
        val activity = activity ?: return
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    /**
     * ミュート削除ダイアログ
     */
    class MuteClearFragment : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val fragment = parentFragment as? MuteManagementFragment ?: return super.onCreateDialog(savedInstanceState)
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
