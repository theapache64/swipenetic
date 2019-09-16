package com.theapache64.swipenetic.ui.activities.intro

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.theapache64.swipenetic.R
import com.theapache64.swipenetic.databinding.ActivityIntroBinding
import com.theapache64.swipenetic.ui.activities.main.MainActivity
import com.theapache64.twinkill.ui.activities.base.BaseAppCompatActivity
import com.theapache64.twinkill.utils.extensions.bindContentView
import dagger.android.AndroidInjection
import javax.inject.Inject

class IntroActivity : BaseAppCompatActivity(), IntroHandler {

    companion object {
        const val ID = R.id.INTRO_ACITIVITY_ID
        fun getStartIntent(context: Context): Intent {
            return Intent(context, IntroActivity::class.java).apply {
                // data goes here
            }
        }
    }


    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private lateinit var binding: ActivityIntroBinding
    private lateinit var viewModel: IntroViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        binding = bindContentView(R.layout.activity_intro)
        viewModel = ViewModelProviders.of(this, factory).get(IntroViewModel::class.java)

        // Watching for tile
        viewModel.getIsTileAdded().observe(this, Observer { isAdded ->
            if (isAdded) {
                startActivity(MainActivity.getStartIntent(this))
                finish()
            }
        })

        binding.handler = this
        binding.viewModel = viewModel
    }

    override fun onResume() {
        super.onResume()

        viewModel.checkIfTileAdded()
    }
}
