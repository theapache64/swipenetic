package com.theapache64.swipenetic.data.local.typeconverters

import androidx.room.TypeConverter
import com.theapache64.swipenetic.models.SwipeTag

class SwipeTagConverter {

    @TypeConverter
    fun fromSwipeTag(swipeTag: SwipeTag?): String? {
        return swipeTag?.name
    }

    @TypeConverter
    fun toSwipeTag(string: String?): SwipeTag? {
        return if (string == null) null else SwipeTag.valueOf(string)
    }
}