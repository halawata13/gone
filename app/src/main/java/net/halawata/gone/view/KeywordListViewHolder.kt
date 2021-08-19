package net.halawata.gone.view

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import net.halawata.gone.R

class KeywordListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var titleTextView: TextView = itemView.findViewById(R.id.keywordRecyclerViewItemTitle)
    var clearImageView: ImageView = itemView.findViewById(R.id.keywordRecyclerViewItemClear)
}
