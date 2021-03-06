package com.theapache64.swipenetic.utils

import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.theapache64.swipenetic.App
import com.theapache64.swipenetic.R
import com.theapache64.swipenetic.data.repositories.GeneralPrefRepository
import com.theapache64.swipenetic.data.repositories.SwipeRepository
import com.theapache64.swipenetic.models.SwipeOutTag
import com.theapache64.swipenetic.workers.OutTagAlertWorker
import com.theapache64.twinkill.logger.info
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SwipeAlertManager @Inject constructor(
    private val swipeRepository: SwipeRepository,
    private val generalPrefRepository: GeneralPrefRepository
) {

    fun scheduleAlert(context: Context, swipeOutTag: SwipeOutTag) {

        swipeRepository.getInTimeAndOutTags(Date()) { _: Long, outTags: Map<SwipeOutTag, Long> ->

            val totalTimeSpentToday =
                outTags.filter { it.key == swipeOutTag }
                    .entries.firstOrNull()
                    ?.value ?: 0

            val timeRemaining =
                swipeOutTag.maxTimeAllowedPerDayInMillis - totalTimeSpentToday

            if (timeRemaining > 0) {
                // has balance time
                notifyRemSwipeOut(context, timeRemaining, swipeOutTag)
                scheduleNotification(context, timeRemaining, swipeOutTag)
            } else {
                notifyOverSwipeOut(context, swipeOutTag)
            }
        }
    }

    private fun notifyRemSwipeOut(
        context: Context,
        timeRemainingInMillis: Long,
        swipeOutTag: SwipeOutTag
    ) {
        val minRem = TimeUnit.MILLISECONDS.toMinutes(timeRemainingInMillis)
        val minOrMins = if (minRem <= 1) "minute" else "minutes"
        val message =
            context.getString(R.string.notif_msg_rem_time, minRem, minOrMins, swipeOutTag.label)
        notify(context, R.string.notif_title_balance_time, message, false)
    }

    private fun notifyOverSwipeOut(context: Context, swipeOutTag: SwipeOutTag) {
        val timeSpent = TimeUnit.MILLISECONDS.toMinutes(swipeOutTag.maxTimeAllowedPerDayInMillis)
        val message =
            context.getString(R.string.notif_msg_over_swipe_out, timeSpent, swipeOutTag.label)
        notify(context, R.string.notif_title_over_swipe, message, true)
    }

    private fun notify(
        context: Context,
        @StringRes title: Int,
        message: String,
        isCritical: Boolean
    ) {

        val nb = NotificationCompat.Builder(context, App.CHANNEL_ALERT_ID)
            .setSmallIcon(R.drawable.ic_clock)
            .setContentTitle(context.getString(title))
            .setContentText(message)

        if (isCritical) {
            nb.setDefaults(NotificationCompat.DEFAULT_LIGHTS and NotificationCompat.DEFAULT_VIBRATE)
                .setSound(Uri.parse("android.resource://${context.packageName}/${R.raw.danger}"))
        } else {
            nb.setDefaults(NotificationCompat.DEFAULT_ALL)
        }

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(4355, nb.build())
    }

    private fun scheduleNotification(
        context: Context,
        delay: Long,
        outTagName: SwipeOutTag
    ) {

        info("Notification scheduled on ${Date(System.currentTimeMillis() + delay)}")

        val inputData = Data.Builder()
            .putString(OutTagAlertWorker.KEY_OUT_TAG, outTagName.name)
            .build()

        val workerRequest =
            OneTimeWorkRequest.Builder(OutTagAlertWorker::class.java)
                .setInputData(inputData)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .build()

        WorkManager.getInstance(context)
            .enqueue(workerRequest)

        generalPrefRepository.saveWorkId(workerRequest.id.toString())
    }
}