package com.theapache64.swipenetic.data.local.typeconverters

import androidx.room.TypeConverter
import com.theapache64.swipenetic.data.local.entities.Swipe

class SwipeTypeConvertor {

    @TypeConverter
    fun fromSwipeType(swipeType: Swipe.Type): String {
        return swipeType.name
    }

    @TypeConverter
    fun toSwipeType(swipeType: String): Swipe.Type {
        return if (swipeType == fromSwipeType(Swipe.Type.IN)) Swipe.Type.IN else Swipe.Type.OUT
    }

}