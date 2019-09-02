package com.theapache64.swipenetic.ui.activities.chart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.theapache64.swipenetic.R
import com.theapache64.swipenetic.databinding.ActivityChartBinding
import com.theapache64.swipenetic.models.SwipeTag
import com.theapache64.twinkill.ui.activities.base.BaseAppCompatActivity
import com.theapache64.twinkill.utils.extensions.bindContentView
import dagger.android.AndroidInjection
import java.io.Serializable
import java.util.*
import javax.inject.Inject

class ChartActivity : BaseAppCompatActivity(), ChartHandler {

    companion object {

        private const val KEY_DATE = "date"

        fun getStartIntent(
            context: Context,
            date: Date
        ): Intent {
            return Intent(context, ChartActivity::class.java).apply {
                // data goes here
                putExtra(KEY_DATE, date)
            }
        }
    }


    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private lateinit var binding: ActivityChartBinding
    private lateinit var viewModel: ChartViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        binding = bindContentView(R.layout.activity_chart)
        setSupportActionBar(binding.toolbar)
        viewModel = ViewModelProviders.of(this, factory).get(ChartViewModel::class.java)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.handler = this
        binding.viewModel = viewModel

        val date = intent.getStringExtra(KEY_DATE) as Date
        viewModel.init(date)

        viewModel.getChartData().observe(this, Observer { data ->
            val pieDataSet = PieDataSet(
                data.toMutableList(),
                "Swipe Data"
            )

            binding.pcSwipe.data = PieData(pieDataSet)
            binding.pcSwipe.invalidate()
        })

        viewModel.loadChartData()
    }

}
