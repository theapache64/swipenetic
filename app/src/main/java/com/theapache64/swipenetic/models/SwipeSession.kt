package com.theapache64.swipenetic.models

import com.theapache64.swipenetic.data.local.entities.Swipe

data class SwipeSession(
    val type: Swipe.Type,
    val duration: String,
    val tag: SwipeTag?,
    val timeFrom: String,
    val timeTo: String
) {
    fun isTypeIn(): Boolean {
        return type == Swipe.Type.IN
    }
}