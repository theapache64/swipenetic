package com.theapache64.swipenetic.services


import android.service.quicksettings.TileService
import com.theapache64.swipenetic.data.local.entities.Swipe
import com.theapache64.swipenetic.ui.fragments.SwipeTagsDialog
import java.util.*


class SwipeneticTileService : TileService() {

    override fun onClick() {
        super.onClick()

        startService(
            LiveTimeUpdateService.getStartIntent(
                this,
                LiveTimeUpdateService.ACTION_CLICK
            )
        )
    }

    override fun onStartListening() {
        super.onStartListening()

        startService(
            LiveTimeUpdateService.getStartIntent(
                this,
                LiveTimeUpdateService.ACTION_LISTEN
            )
        )


    }
}