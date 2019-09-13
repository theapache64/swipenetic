package com.theapache64.swipenetic.models

import com.theapache64.swipenetic.data.local.entities.Swipe
import com.theapache64.swipenetic.utils.DateUtils2

data class SwipeSession(
    val type: Swipe.Type,
    var duration: Long,
    val outTag: SwipeOutTag?,
    val timeFrom: String,
    var timeTo: String,
    val startSwipe: Swipe,
    val endSwipe: Swipe?
) {
    var durationString = DateUtils2.getDuration(duration)

    fun isTypeIn(): Boolean {
        return type == Swipe.Type.IN
    }

    fun update(duration: Long, timeTo: String) {
        this.duration = duration
        this.timeTo = timeTo
        this.durationString = DateUtils2.getDuration(duration)
    }
}