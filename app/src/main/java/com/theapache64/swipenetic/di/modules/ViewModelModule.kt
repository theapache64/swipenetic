package com.theapache64.swipenetic.di.modules
import com.theapache64.swipenetic.ui.activities.intro.IntroViewModel

import androidx.lifecycle.ViewModel
import com.theapache64.swipenetic.ui.activities.chart.ChartViewModel
import com.theapache64.swipenetic.ui.activities.main.MainViewModel
import com.theapache64.swipenetic.ui.activities.splash.SplashViewModel
import com.theapache64.swipenetic.ui.activities.summary.SummaryViewModel
import com.theapache64.twinkill.di.modules.BaseViewModelModule
import com.theapache64.twinkill.utils.viewmodel.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Module(includes = [BaseViewModelModule::class])
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel::class)
    abstract fun bindSplashViewModel(viewModel: SplashViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(viewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SummaryViewModel::class)
    abstract fun bindSummaryViewModel(viewModel: SummaryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ChartViewModel::class)
    abstract fun bindChartViewModel(viewModel: ChartViewModel): ViewModel

@Binds
@IntoMap
@ViewModelKey(IntroViewModel::class)
abstract fun bindIntroViewModel(viewModel: IntroViewModel): ViewModel

}