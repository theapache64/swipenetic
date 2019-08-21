package com.theapache64.swipenetic.data.local.typeconverters

import androidx.room.TypeConverter
import java.util.*

class DateConvertor {
    @TypeConverter
    fun toDate(long: Long): Date {
        return Date(long)
    }

    @TypeConverter
    fun fromDate(date: Date): Long {
        return date.time
    }
}