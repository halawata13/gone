package net.halawata.gone.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import net.halawata.gone.R

class ConfigFragment : Fragment(), AdapterView.OnItemClickListener {

    private var listener: OnItemClickListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is OnItemClickListener) {
            listener = context
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_config, container, false)
        val activity = activity ?: return view

        val adapter = ArrayAdapter(activity, android.R.layout.simple_list_item_1, resources.getStringArray(R.array.config_list))
        val configListView = view.findViewById<ListView>(R.id.configListView)
        configListView.adapter = adapter
        configListView.onItemClickListener = this

        return view
    }

    override fun onDetach() {
        super.onDetach()

        listener = null
    }

    override fun onItemClick(parent: AdapterView<*>?, v: View?, position: Int, id: Long) {
        val text = resources.getStringArray(R.array.config_list)[position]
        onItemClicked(text)
    }

    private fun onItemClicked(text: String) {
        listener?.onItemClick(text)
    }

    interface OnItemClickListener {
        fun onItemClick(text: String)
    }
}
