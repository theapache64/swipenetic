package com.theapache64.swipenetic.workers

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.theapache64.swipenetic.App
import com.theapache64.swipenetic.R
import com.theapache64.swipenetic.models.SwipeOutTag
import com.theapache64.twinkill.logger.debug
import java.util.concurrent.TimeUnit

class OutTagAlertWorker(
    context: Context,
    workerParams: WorkerParameters
) :
    Worker(context, workerParams) {
    companion object {
        const val KEY_OUT_TAG = "out_tag"
    }

    override fun doWork(): Result {

        debug("OutTagAlertWorker doWork called (y)")

        val outTagName = inputData.getString(KEY_OUT_TAG)
        if (outTagName != null) {

            val swipeOutTag = SwipeOutTag.valueOf(outTagName)
            val message = getMessage(swipeOutTag)
            val notification = getNotification(message)

            val notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(1, notification)
        }

        return Result.success()
    }

    private fun getNotification(message: String): Notification {
        return NotificationCompat.Builder(applicationContext, App.CHANNEL_TIMER_ID)
            .setSmallIcon(R.drawable.ic_clock)
            .setContentTitle(applicationContext.getString(R.string.notif_title_alert))
            .setContentText(message)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()
    }


    private fun getMessage(outTag: SwipeOutTag): String {
        val timeSpentInMinutes =
            TimeUnit.MILLISECONDS.toMinutes(outTag.maxTimeAllowedPerDayInMillis)
        return "You have spent $timeSpentInMinutes minutes for ${outTag.label}"
    }
}