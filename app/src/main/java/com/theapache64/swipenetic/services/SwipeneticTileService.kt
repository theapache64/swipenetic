package com.theapache64.swipenetic.services

import android.service.quicksettings.TileService
import com.theapache64.swipenetic.data.repositories.GeneralPrefRepository
import dagger.android.AndroidInjection
import javax.inject.Inject

class SwipeneticTileService : TileService() {

    @Inject
    lateinit var prefRepo: GeneralPrefRepository

    override fun onCreate() {
        super.onCreate()
        AndroidInjection.inject(this)
    }

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

        prefRepo.setIsTileAdded(true)
    }

    override fun onTileRemoved() {
        super.onTileRemoved()

        prefRepo.setIsTileAdded(false)
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