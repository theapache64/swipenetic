package com.theapache64.swipenetic


import android.app.*
import android.content.Context
import android.os.Build
import com.theapache64.swipenetic.di.components.DaggerAppComponent
import com.theapache64.swipenetic.di.modules.AppModule
import com.theapache64.twinkill.TwinKill
import com.theapache64.twinkill.di.modules.ContextModule
import com.theapache64.twinkill.googlesans.GoogleSans
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.HasServiceInjector
import javax.inject.Inject

class App : Application(), HasActivityInjector, HasServiceInjector {

    companion object {
        const val CHANNEL_TIMER_ID = "timer"
    }

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
            .contextModule(ContextModule(this))
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

        // Create notification channel
        createNotificationChannel()
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_timer_name)
            val descriptionText = getString(R.string.channel_time_desc)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_TIMER_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


}
