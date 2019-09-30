package com.theapache64.swipenetic.ui.activities.splash


import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.theapache64.swipenetic.R
import com.theapache64.swipenetic.databinding.ActivitySplashBinding
import com.theapache64.swipenetic.ui.activities.intro.IntroActivity
import com.theapache64.swipenetic.ui.activities.main.MainActivity
import com.theapache64.twinkill.ui.activities.base.BaseAppCompatActivity
import com.theapache64.twinkill.utils.extensions.bindContentView
import dagger.android.AndroidInjection
import javax.inject.Inject

class SplashActivity : BaseAppCompatActivity(), SplashHandler {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        val binding = bindContentView<ActivitySplashBinding>(R.layout.activity_splash)
        val viewModel = ViewModelProviders.of(this, factory).get(SplashViewModel::class.java)
        binding.viewModel = viewModel
        binding.handler = this

        // Watching activity launch command
        viewModel.getLaunchActivityEvent().observe(this, Observer { activityId ->

            when (activityId) {

                MainActivity.ID -> {
                    startActivity(MainActivity.getStartIntent(this))
                }

                IntroActivity.ID -> {
                    startActivity(IntroActivity.getStartIntent(this))
                }

                else -> throw IllegalArgumentException("Undefined activity id $activityId")
            }

            finish()

        })

        // Starting splash timer
        Handler().postDelayed({
            viewModel.goToNextScreen()
        }, SPLASH_DURATION)



    }


    companion object {
        private const val SPLASH_DURATION = 1000L
    }

}
