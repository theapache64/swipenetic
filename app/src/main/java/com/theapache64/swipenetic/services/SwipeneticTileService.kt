package com.theapache64.swipenetic.services

import android.service.quicksettings.TileService
import dagger.android.AndroidInjection

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

    override fun onTileAdded() {
        super.onTileAdded()


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