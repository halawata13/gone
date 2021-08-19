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
import net.halawata.gone.service.DatabaseHelper
import net.halawata.gone.service.DateRangeService

class DateRangeManagementFragment : Fragment(), AdapterView.OnItemClickListener {

    private var dateRangeService: DateRangeService? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        dateRangeService = DateRangeService(DatabaseHelper(context))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_date_range_management, container, false)
        val activity = activity ?: return view
        val dateRangeService = dateRangeService ?: return view

        val dateRange = dateRangeService.get()
        val adapter = ArrayAdapter(activity, android.R.layout.simple_list_item_single_choice, dateRangeService.getDateRangeValue())
        val dateRangeListView = view.findViewById<ListView>(R.id.dateRangeListView)

        dateRangeListView.adapter = adapter
        dateRangeListView.choiceMode = ListView.CHOICE_MODE_SINGLE
        dateRangeListView.setItemChecked(dateRange.ordinal, true)
        dateRangeListView.onItemClickListener = this

        return view
    }

    override fun onDetach() {
        super.onDetach()

        dateRangeService = null
    }

    override fun onItemClick(parent: AdapterView<*>?, v: View?, position: Int, id: Long) {
        val dateRange = DateRangeService.DateRange.values()[position]
        dateRangeService?.update(dateRange)
    }
}
