package net.halawata.gone.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import net.halawata.gone.R
import net.halawata.gone.entity.SideMenuItem
import net.halawata.gone.service.SideMenuService
import net.halawata.gone.view.DrawerListAdapter

class SideMenuFragment : Fragment(), ExpandableListView.OnChildClickListener, ExpandableListView.OnGroupClickListener {

    private var selectedItem: SideMenuItem? = null
    private var listener: OnSideMenuFragmentListener? = null
    private var drawerListAdapter: DrawerListAdapter? = null
    private lateinit var drawerListView: ExpandableListView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_side_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = activity ?: return
        drawerListView = view.findViewById(R.id.drawerListView)

        drawerListAdapter = DrawerListAdapter(activity, arrayListOf(), arrayListOf(arrayListOf()), R.layout.drawer_section_item, R.layout.drawer_list_item).also {
            drawerListView.setAdapter(it)
            drawerListView.setOnChildClickListener(this)
            drawerListView.setOnGroupClickListener(this)
        }

        updateSideMenu()
        onSideMenuSelected()
    }

    /**
     * サイドメニュー選択時
     */
    override fun onChildClick(parent: ExpandableListView?, v: View?, groupPosition: Int, childPosition: Int, id: Long): Boolean {
        val drawerListAdapter = drawerListAdapter ?: return true
        selectedItem = drawerListAdapter.data[groupPosition][childPosition]
        onSideMenuSelected()

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
    fun updateSideMenu() {
        val activity = activity ?: return
        val drawerListAdapter = drawerListAdapter ?: return
        val sideMenu = SideMenuService(activity).getSideMenu()

        selectedItem = sideMenu.items[0].firstOrNull()
        drawerListAdapter.sectionData = sideMenu.sections
        drawerListAdapter.data = sideMenu.items
        drawerListAdapter.notifyDataSetChanged()

        // 最初から開いた状態に
        for (i in 0 until drawerListAdapter.groupCount) {
            drawerListView.expandGroup(i, false)
        }
    }

    private fun onSideMenuSelected() {
        selectedItem?.let {
            listener?.onSideMenuSelected(it)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is OnSideMenuFragmentListener) {
            listener = context
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        selectedItem = null
        drawerListAdapter = null
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnSideMenuFragmentListener {
        fun onSideMenuSelected(selectedItem: SideMenuItem)
    }
}
