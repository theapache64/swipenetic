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
    var durationString =
        DateUtils2.getDuration(kotlin.math.abs(duration)) ?: SwipeSummary.KEY_FORGOT_TO_OUT

    init {
        if (duration < 0) {
            throw IllegalArgumentException("Duration can't be negative ($duration)")
        }

        if (durationString == SwipeSummary.KEY_FORGOT_TO_OUT) {
            timeTo = "🤷🏽‍♂️"
        }
    }


    fun isTypeIn(): Boolean {
        return type == Swipe.Type.IN
    }

    fun update(duration: Long, timeTo: String) {
        this.duration = duration
        this.durationString = DateUtils2.getDuration(duration) ?: SwipeSummary.KEY_FORGOT_TO_OUT
        this.timeTo = timeTo
    }
}