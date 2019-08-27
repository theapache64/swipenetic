package com.theapache64.swipenetic.models

import androidx.annotation.DrawableRes
import com.theapache64.swipenetic.R

enum class SwipeTag(
    val label: String,
    @DrawableRes val image: Int
) {
    PANTRY("Pantry", R.drawable.ic_fork),
    WASHROOM("Washroom", R.drawable.ic_salah),
    TT("Table Tennis", R.drawable.ic_table_tennis),
    LUNCH("Lunch", R.drawable.ic_serving_dish),
    COFFEE("Coffee", R.drawable.ic_coffee_cup),
    OTHER("Other", R.drawable.ic_confetti)
}