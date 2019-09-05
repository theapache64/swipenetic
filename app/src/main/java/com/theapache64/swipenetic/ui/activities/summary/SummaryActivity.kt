package com.theapache64.swipenetic.ui.activities.summary

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.theapache64.swipenetic.R
import com.theapache64.swipenetic.databinding.ActivitySummaryBinding
import com.theapache64.swipenetic.ui.activities.chart.ChartActivity
import com.theapache64.swipenetic.ui.adapters.SwipeSummaryAdapter
import com.theapache64.twinkill.logger.info
import com.theapache64.twinkill.ui.activities.base.BaseAppCompatActivity
import com.theapache64.twinkill.utils.extensions.bindContentView
import dagger.android.AndroidInjection
import java.util.*
import javax.inject.Inject

class SummaryActivity : BaseAppCompatActivity(), SummaryHandler {

    companion object {
        private const val KEY_DATE = "date"
        fun getStartIntent(context: Context, date: Date): Intent {
            return Intent(context, SummaryActivity::class.java).apply {
                // data goes here
                putExtra(KEY_DATE, date)
            }
        }
    }


    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private lateinit var binding: ActivitySummaryBinding
    private lateinit var viewModel: SummaryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        binding = bindContentView(R.layout.activity_summary)
        setSupportActionBar(binding.toolbar)
        viewModel = ViewModelProviders.of(this, factory).get(SummaryViewModel::class.java)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        binding.handler = this
        binding.viewModel = viewModel

        // Watching for subtitle
        viewModel.getToolbarSubtitle().observe(this, Observer {
            binding.toolbar.apply {
                post {
                    subtitle = it
                }
            }
        })

        viewModel.getSwipeSummary().observe(this, Observer {

            info("Swipe summary loaded : $it")
            val adapter = SwipeSummaryAdapter(this, it) { position ->
                info("Clicked on $position")
            }

            binding.rvSwipeSummary.adapter = adapter
        })

        val date = intent.getSerializableExtra(KEY_DATE) as Date
        viewModel.loadSwipeSummary(date)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_summary, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_show_chart -> {
                startActivity(ChartActivity.getStartIntent(this, viewModel.date))
            }
            else -> return super.onOptionsItemSelected(item)
        }

        return true

    }
}
