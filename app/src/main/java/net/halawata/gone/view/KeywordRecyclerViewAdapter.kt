package net.halawata.gone.view

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import net.halawata.gone.entity.KeywordItem
import net.halawata.gone.R

open class KeywordRecyclerViewAdapter(var data: ArrayList<KeywordItem>) : RecyclerView.Adapter<KeywordListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeywordListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.keyword_recycler_view_item, parent, false)
        val holder = KeywordListViewHolder(view)

        holder.clearImageView.setOnClickListener {
            onClearClicked(holder.adapterPosition)
        }

        return holder
    }

    override fun onBindViewHolder(holder: KeywordListViewHolder, position: Int) {
        holder.titleTextView.text = data[position].title
    }

    override fun getItemCount(): Int {
        return data.size
    }

    open fun onClearClicked(position: Int) {
    }
}
