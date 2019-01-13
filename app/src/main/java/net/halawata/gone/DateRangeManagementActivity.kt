package net.halawata.gone

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import kotlinx.android.synthetic.main.activity_date_range_management.*
import net.halawata.gone.service.DatabaseHelper
import net.halawata.gone.service.DateRangeService

class DateRangeManagementActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    lateinit var dateRangeService: DateRangeService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_date_range_management)

        dateRangeService = DateRangeService(DatabaseHelper(this))
        val dateRange = dateRangeService.get()

        supportActionBar?.title = getString(R.string.date_range_management_activity_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_24)

        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, dateRangeService.getDateRangeValue())
        dateRangeListView.adapter = adapter
        dateRangeListView.choiceMode = ListView.CHOICE_MODE_SINGLE
        dateRangeListView.setItemChecked(dateRange.ordinal, true)
        dateRangeListView.onItemClickListener = this
    }

    override fun onItemClick(parent: AdapterView<*>?, v: View?, position: Int, id: Long) {
        val dateRange = DateRangeService.DateRange.values()[position]
        dateRangeService.update(dateRange)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
