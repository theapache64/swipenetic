package com.theapache64.swipenetic.di.modules
import com.theapache64.swipenetic.ui.activities.intro.IntroActivity


import com.theapache64.swipenetic.ui.activities.chart.ChartActivity
import com.theapache64.swipenetic.ui.activities.main.MainActivity
import com.theapache64.swipenetic.ui.activities.splash.SplashActivity
import com.theapache64.swipenetic.ui.activities.summary.SummaryActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * To hold activities to support AndroidInjection call from dagger.
 */
@Module
abstract class ActivitiesBuilderModule {

    @ContributesAndroidInjector
    abstract fun getSplashActivity(): SplashActivity

    @ContributesAndroidInjector
    abstract fun getMainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun getSummaryActivity(): SummaryActivity

    @ContributesAndroidInjector
    abstract fun getChartActivity(): ChartActivity

@ContributesAndroidInjector
abstract fun getIntroActivity(): IntroActivity

}