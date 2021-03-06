package com.theapache64.swipenetic


import android.app.*
import android.content.Context
import android.os.Build
import androidx.annotation.StringRes
import com.theapache64.swipenetic.di.components.DaggerAppComponent
import com.theapache64.swipenetic.di.modules.AppModule
import com.theapache64.twinkill.TwinKill
import com.theapache64.twinkill.di.modules.ContextModule
import com.theapache64.twinkill.googlesans.GoogleSans
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class App : Application(), HasAndroidInjector {

    companion object {
        const val CHANNEL_TIMER_ID = "timer"
        const val CHANNEL_ALERT_ID = "alert"
    }

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var serviceInjector: DispatchingAndroidInjector<Service>


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
        createNotificationChannel(
            CHANNEL_TIMER_ID,
            R.string.channel_timer_name,
            R.string.channel_time_desc,
            NotificationManager.IMPORTANCE_DEFAULT
        )

        createNotificationChannel(
            CHANNEL_ALERT_ID,
            R.string.channel_alert_name,
            R.string.channel_alert_desc,
            NotificationManager.IMPORTANCE_DEFAULT
        )

        // This is some comment
    }

    private fun createNotificationChannel(
        channelId: String,
        @StringRes nameRes: Int,
        @StringRes descRes: Int,
        importance: Int
    ) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // Creating timer channel
            val name = getString(nameRes)
            val descriptionText = getString(descRes)
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    @Inject lateinit var androidInjector : DispatchingAndroidInjector<Any>
    override fun androidInjector(): AndroidInjector<Any> = androidInjector


}
