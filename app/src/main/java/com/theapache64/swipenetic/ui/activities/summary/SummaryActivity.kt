package com.theapache64.swipenetic.ui.activities.summary

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.theapache64.swipenetic.R
import com.theapache64.swipenetic.databinding.ActivitySummaryBinding
import com.theapache64.twinkill.ui.activities.base.BaseAppCompatActivity
import com.theapache64.twinkill.utils.extensions.bindContentView
import dagger.android.AndroidInjection
import javax.inject.Inject

class SummaryActivity : BaseAppCompatActivity(), SummaryHandler {

    companion object {
        fun getStartIntent(context: Context): Intent {
            return Intent(context, SummaryActivity::class.java).apply {
                // data goes here
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
        viewModel = ViewModelProviders.of(this, factory).get(SummaryViewModel::class.java)

        binding.handler = this
        binding.viewModel = viewModel

        viewModel.getSwipeSummary().obsever(this, Observer {

        })
    }
}
