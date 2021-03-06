package com.theapache64.swipenetic.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DateUtils2 {

    /**
     * Sample : 31-12-2019
     */
    private val FORMAT_DD_MM_YYYY by lazy { SimpleDateFormat("dd-MM-yyyy", Locale.US) }


    private val FORMAT_HH_mm_ss by lazy {
        getTimeZoneWithUTC("HH:mm:ss")

    }

    private val FORMAT_h_mm_a by lazy {
        val dateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        dateFormat
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

    fun getDuration(durationInMillis: Long): String? {

        return when (val seconds = TimeUnit.MILLISECONDS.toSeconds(durationInMillis)) {
            0L, 1L -> "$seconds second"
            in 1..59 -> "$seconds seconds"
            60L -> "1 minute"
            in 61..3599 -> {
                // minute and seconds
                val min = seconds / 60
                val sec = seconds % 60
                "$min min $sec sec."
            }

            3600L -> "1 hour"
            in 3601..86399 -> {
                // hour and minute
                val hour = seconds / 3600
                val minute = (seconds % 3600) / 60
                "$hour hrs $minute min"
            }

            else -> {
                null
            }
        }
    }

    fun fromDDMMYYY(date: String): Calendar {
        val cal = Calendar.getInstance()
        cal.time = FORMAT_DD_MM_YYYY.parse(date)!!
        return cal
    }
}