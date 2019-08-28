package com.theapache64.swipenetic.services


import android.service.quicksettings.TileService


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