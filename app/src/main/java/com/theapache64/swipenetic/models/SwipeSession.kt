package com.theapache64.swipenetic.models

import com.theapache64.swipenetic.data.local.entities.Swipe
import com.theapache64.swipenetic.utils.DateUtils2

data class SwipeSession(
    val type: Swipe.Type,
    val duration: Long,
    val tag: SwipeTag?,
    val timeFrom: String,
    val timeTo: String
) {
    val durationString = DateUtils2.getDuration(duration)
    fun isTypeIn(): Boolean {
        return type == Swipe.Type.IN
    }
}