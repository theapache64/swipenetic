package com.theapache64.swipenetic.models

import androidx.annotation.DrawableRes

class SwipeSummary(
    @DrawableRes val image: Int,
    val label: String,
    val value: String
) {
    companion object {
        const val KEY_FORGOT_TO_OUT = "😒"
    }
}