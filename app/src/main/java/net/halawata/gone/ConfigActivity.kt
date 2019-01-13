package net.halawata.gone

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_config.*

class ConfigActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)

        supportActionBar?.title = getString(R.string.config_activity_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_24)

        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, resources.getStringArray(R.array.config_list))
        configListView.adapter = adapter
        configListView.onItemClickListener = this
    }

    override fun onItemClick(parent: AdapterView<*>?, v: View?, position: Int, id: Long) {
        val text = resources.getStringArray(R.array.config_list)[position]

        when (text) {
            getString(R.string.keyword_management_activity_title) -> KeywordManagementActivity::class.java
            getString(R.string.mute_management_activity_title) -> MuteManagementActivity::class.java
            getString(R.string.date_range_management_activity_title) -> DateRangeManagementActivity::class.java
            else -> null
        }?.let {
            val intent = Intent(this, it)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
