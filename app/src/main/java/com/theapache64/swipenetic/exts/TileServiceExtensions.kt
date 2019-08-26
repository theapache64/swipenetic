package com.theapache64.swipenetic.exts

import android.service.quicksettings.Tile

fun Tile.updateTile(newState: Int, newLabel: String) {
    apply {
        state = newState
        label = newLabel
    }.updateTile()
}