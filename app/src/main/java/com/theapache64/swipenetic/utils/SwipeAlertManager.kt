package com.theapache64.swipenetic.utils

import android.app.NotificationManager
import android.content.Context
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
        val message = "You have $minRem $minOrMins remaining in ${swipeOutTag.label}"
        notify(context, message)
    }

    private fun notifyOverSwipeOut(context: Context, swipeOutTag: SwipeOutTag) {
        val timeSpent = TimeUnit.MILLISECONDS.toMinutes(swipeOutTag.maxTimeAllowedPerDayInMillis)
        val message = "You have spent more than $timeSpent minutes in ${swipeOutTag.label}"
        notify(context, message)
    }

    private fun notify(context: Context, message: String) {

        val n = NotificationCompat.Builder(context, App.CHANNEL_ALERT_ID)
            .setSmallIcon(R.drawable.ic_clock)
            .setContentTitle(context.getString(R.string.notif_title_over_swipe))
            .setContentText(message)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(Math.random().toInt(), n)
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