package net.halawata.gone.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import net.halawata.gone.R
import net.halawata.gone.fragment.ConfigFragment

class ConfigActivity : AppCompatActivity(), ConfigFragment.OnItemClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)

        supportActionBar?.title = getString(R.string.config_activity_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_24)

        setResult(Activity.RESULT_OK)
    }

    override fun onItemClick(text: String) {
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
