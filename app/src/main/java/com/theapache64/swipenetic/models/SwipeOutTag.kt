package com.theapache64.swipenetic.models

import androidx.annotation.DrawableRes
import com.theapache64.swipenetic.R
import java.io.Serializable
import java.util.concurrent.TimeUnit

enum class SwipeOutTag(
    val label: String,
    @DrawableRes val image: Int,
    val maxTimeAllowedPerDay: Long
) : Serializable {
    PANTRY("Pantry", R.drawable.ic_fork, TimeUnit.MINUTES.toMillis(15)),
    WASHROOM("Washroom", R.drawable.ic_salah, TimeUnit.MINUTES.toMillis(5)),
    TT("Table Tennis", R.drawable.ic_table_tennis, TimeUnit.MINUTES.toMillis()),
    LUNCH("Lunch", R.drawable.ic_serving_dish),
    COFFEE("Coffee", R.drawable.ic_coffee_cup),
    OTHER("Other", R.drawable.ic_confetti)
}