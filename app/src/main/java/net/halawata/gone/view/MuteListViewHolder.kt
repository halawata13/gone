package net.halawata.gone.view

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import net.halawata.gone.R

class MuteListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var titleTextView: TextView = itemView.findViewById(R.id.muteRecyclerViewItemTitle)
    var clearImageView: ImageView = itemView.findViewById(R.id.muteRecyclerViewItemClear)
}
