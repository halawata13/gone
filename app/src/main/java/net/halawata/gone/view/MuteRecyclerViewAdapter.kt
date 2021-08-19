package net.halawata.gone.view

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import net.halawata.gone.entity.KeywordItem
import net.halawata.gone.R

open class MuteRecyclerViewAdapter(var data: ArrayList<String>) : RecyclerView.Adapter<MuteListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MuteListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.mute_recycler_view_item, parent, false)
        val holder = MuteListViewHolder(view)

        holder.clearImageView.setOnClickListener {
            onClearClicked(holder.adapterPosition)
        }

        return holder
    }

    override fun onBindViewHolder(holder: MuteListViewHolder, position: Int) {
        holder.titleTextView.text = data[position]
    }

    override fun getItemCount(): Int {
        return data.size
    }

    open fun onClearClicked(position: Int) {
    }
}
