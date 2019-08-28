package com.theapache64.swipenetic.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

class SwipeSummary(
    @DrawableRes val image: Int,
    @StringRes val label: Int,
    val value: String
)