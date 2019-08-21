package com.theapache64.swipenetic


import android.app.Activity
import android.app.Application
import android.app.Service
import com.theapache64.swipenetic.di.components.DaggerAppComponent
import com.theapache64.swipenetic.di.modules.AppModule
import com.theapache64.twinkill.TwinKill
import com.theapache64.twinkill.googlesans.GoogleSans
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.HasServiceInjector
import javax.inject.Inject

class App : Application(), HasActivityInjector, HasServiceInjector {


    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var serviceInjector: DispatchingAndroidInjector<Service>

    override fun serviceInjector(): AndroidInjector<Service> = serviceInjector

    override fun activityInjector(): AndroidInjector<Activity> = activityInjector

    override fun onCreate() {
        super.onCreate()

        // Dagger
        DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
            .inject(this)

        // TwinKill
        TwinKill.init(
            TwinKill
                .builder()
                .setDefaultFont(GoogleSans.Regular)
                .build()
        )

    }


}
