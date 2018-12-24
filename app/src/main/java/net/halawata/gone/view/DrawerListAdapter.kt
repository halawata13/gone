package net.halawata.gone.view

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import net.halawata.gone.R
import net.halawata.gone.entity.SideMenuItem

class DrawerListAdapter(private val context: Context, var sectionData: List<String>, var data: List<List<SideMenuItem>>, private val sectionResource: Int, private val resource: Int): BaseExpandableListAdapter() {

    override fun getGroupCount(): Int {
        return data.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return data[groupPosition].size
    }

    override fun getGroup(groupPosition: Int): List<SideMenuItem> {
        return data[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): SideMenuItem {
        return data[groupPosition][childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return getGroup(groupPosition).hashCode().toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return data[groupPosition][childPosition].id
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val activity = context as Activity
        val view = convertView ?: activity.layoutInflater.inflate(sectionResource, null)

        (view.findViewById(R.id.sectionTitle) as TextView).text = sectionData[groupPosition]

        return view
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        val activity = context as Activity
        val item = getChild(groupPosition, childPosition)
        val view = convertView ?: activity.layoutInflater.inflate(resource, null)

        (view.findViewById(R.id.listTitle) as TextView).text = item.keyword

        return view
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }
}
