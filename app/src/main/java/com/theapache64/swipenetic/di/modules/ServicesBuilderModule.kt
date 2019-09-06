package com.theapache64.swipenetic.di.modules


import com.theapache64.swipenetic.services.LiveTimeUpdateService
import com.theapache64.swipenetic.services.SwipeneticTileService
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * To hold services to support AndroidInjection call from dagger.
 */
@Module
abstract class ServicesBuilderModule {

    @ContributesAndroidInjector
    abstract fun getSwipeneticTileService(): SwipeneticTileService

    @ContributesAndroidInjector
    abstract fun getLiveTimeUpdateService(): LiveTimeUpdateService

}