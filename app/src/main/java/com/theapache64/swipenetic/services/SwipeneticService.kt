package com.theapache64.swipenetic.services

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast
import com.theapache64.swipenetic.R
import com.theapache64.swipenetic.data.local.entities.Swipe
import com.theapache64.swipenetic.data.repositories.SwipeRepository
import com.theapache64.twinkill.logger.info
import dagger.android.AndroidInjection
import javax.inject.Inject

class SwipeneticService : TileService() {

    @Inject
    lateinit var swipeRepository: SwipeRepository

    override fun onCreate() {
        super.onCreate()
        AndroidInjection.inject(this)
    }

    override fun onTileAdded() {
        super.onTileAdded()
        info("Tile added")
    }

    override fun onTileRemoved() {
        super.onTileRemoved()
        info("Tile removed")
    }

    override fun onClick() {
        super.onClick()

        info("Tile clicked")
        swipeRepository.getLastSwipeToday { lastSwipe ->

            val tileState = qsTile.state

            // Error checks
            if (lastSwipe != null) {

                if (lastSwipe.type == Swipe.Type.IN && tileState != Tile.STATE_ACTIVE) {
                    throw IllegalArgumentException("TSH: Swipe mismatch found. Tile state is inactive when swipe is in")
                }

                if (lastSwipe.type == Swipe.Type.OUT && tileState != Tile.STATE_INACTIVE) {
                    throw IllegalArgumentException("TSH: Swipe mismatch found. Tile state is active when swipe is out ")
                }
            }

            val newState: Int
            val newLabel: String

            if (lastSwipe == null || lastSwipe.type == Swipe.Type.OUT) {
                // user wants to IN
                swipeRepository.insertSwipe(Swipe.Type.IN)
                newState = Tile.STATE_ACTIVE
                newLabel = getString(R.string.tile_label_in)
                Toast.makeText(this.applicationContext, "You're IN", Toast.LENGTH_SHORT).show();
            } else {
                // user wants to OUT
                swipeRepository.insertSwipe(Swipe.Type.OUT)
                newState = Tile.STATE_INACTIVE
                newLabel = getString(R.string.tile_label_out)

                Toast.makeText(this.applicationContext, "You're OUT", Toast.LENGTH_SHORT).show();
            }

            updateTile(newState, newLabel)

        }

    }

    override fun onStartListening() {
        super.onStartListening()

        info("Tile started to listen")

        // Updating tile icon according to current swipe status (IN or OUT)
        swipeRepository.getLastSwipeToday { lastSwipeToday ->

            // Getting new state
            val newLabel: String
            val newState: Int
            if (lastSwipeToday != null && lastSwipeToday.type == Swipe.Type.IN) {
                // user is in
                newLabel = getString(R.string.tile_label_in)
                newState = Tile.STATE_ACTIVE
            } else {
                newLabel = getString(R.string.tile_label_out)
                newState = Tile.STATE_INACTIVE
            }

            // Updating tile
            updateTile(newState, newLabel)
        }


    }

    private fun updateTile(newState: Int, newLabel: String) {
        qsTile.apply {
            state = newState
            label = newLabel
        }.updateTile()
    }

    override fun onStopListening() {
        super.onStopListening()

        info("Tile stopped listening")
    }


}