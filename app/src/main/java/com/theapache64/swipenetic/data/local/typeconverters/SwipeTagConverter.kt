package com.theapache64.swipenetic.data.local.typeconverters

import androidx.room.TypeConverter
import com.theapache64.swipenetic.models.SwipeOutTag

class SwipeTagConverter {

    @TypeConverter
    fun fromSwipeTag(swipeOutTag: SwipeOutTag?): String? {
        return swipeOutTag?.name
    }

    @TypeConverter
    fun toSwipeTag(string: String?): SwipeOutTag? {
        return if (string == null) null else SwipeOutTag.valueOf(string)
    }
}