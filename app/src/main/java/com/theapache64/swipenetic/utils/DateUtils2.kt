package com.theapache64.swipenetic.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DateUtils2 {

    private val FORMAT_HH_mm_ss by lazy {
        getTimeZoneWithUTC("HH:mm:ss")

    }

    private val FORMAT_h_mm_a by lazy {
        getTimeZoneWithUTC("h:mm a")
    }

    private fun getTimeZoneWithUTC(format: String): SimpleDateFormat {
        val dateFormat = SimpleDateFormat(format, Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return dateFormat
    }

    fun tohmma(date: Date): String {
        return FORMAT_h_mm_a.format(date)
    }

    fun toHHmmss(date: Date): String {
        return FORMAT_HH_mm_ss.format(date)
    }

    fun toHHmmss(timestamp: Long): String {
        return toHHmmss(Date(timestamp))
    }

    fun getDuration(durationInMillis: Long): String {
        val seconds = TimeUnit.MILLISECONDS.toSeconds(durationInMillis)

        return when (seconds) {
            0L, 1L -> "$seconds second"
            in 1..59 -> "$seconds seconds"
            60L -> "1 minute"
            in 61..3599 -> {
                // minute and seconds
                val minutes = TimeUnit.SECONDS.toMinutes(seconds)
                val seconds = minutes % 60
                "$minutes min $seconds sec."
            }
            3600L -> "1 hour"
            else -> "NONE"
        }
    }
}