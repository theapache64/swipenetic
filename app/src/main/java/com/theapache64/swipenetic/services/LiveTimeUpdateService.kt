package com.theapache64.swipenetic.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.service.quicksettings.Tile
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.theapache64.swipenetic.App
import com.theapache64.swipenetic.R
import com.theapache64.swipenetic.data.local.entities.Swipe
import com.theapache64.swipenetic.data.repositories.SwipeRepository
import com.theapache64.swipenetic.exts.updateTile
import com.theapache64.swipenetic.utils.DateUtils2
import com.theapache64.swipenetic.utils.Repeater
import com.theapache64.twinkill.logger.info
import dagger.android.AndroidInjection
import javax.inject.Inject

class LiveTimeUpdateService : Service() {

    companion object {

        private var repeater = Repeater(1000)

        private lateinit var tile: Tile
        const val ACTION_LISTEN = "listen"
        const val ACTION_CLICK = "click"

        fun getStartIntent(context: Context, action: String, tile: Tile): Intent {
            this.tile = tile
            return Intent(context, LiveTimeUpdateService::class.java).apply {
                // data goes here
                this.action = action

            }
        }
    }


    @Inject
    lateinit var swipeRepository: SwipeRepository

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        AndroidInjection.inject(this)
    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        info("onStartCommand called")

        when (intent.action) {

            ACTION_LISTEN -> {
                info("Action is listen")
                updateState()
            }

            ACTION_CLICK -> {
                info("Action is click")
                handleClick()
            }

            else -> throw IllegalArgumentException("Undefined action ${intent.action}")
        }

        return START_STICKY
    }

    private fun handleClick() {


        swipeRepository.getLastSwipeToday { lastSwipe ->

            val tileState = tile.state

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

                info("Click is for IN,  starting timer...")
                startInTimeUpdate()
            } else {
                // user wants to OUT
                swipeRepository.insertSwipe(Swipe.Type.OUT)
                newState = Tile.STATE_INACTIVE
                newLabel = getString(R.string.tile_label_out)

                Toast.makeText(this.applicationContext, "You're OUT", Toast.LENGTH_SHORT).show();
                info("Click is for OUT, stopping timer")
                stopInTimeUpdate()
            }

            tile.updateTile(newState, newLabel)
        }
    }

    private fun updateState() {


        info("Updating state...")

        // Updating tile icon according to current swipe status (IN or OUT)
        swipeRepository.getLastSwipeToday { lastSwipeToday ->

            // Getting new state
            val newLabel: String
            val newState: Int
            if (lastSwipeToday != null && lastSwipeToday.type == Swipe.Type.IN) {
                // user is in
                newLabel = getString(R.string.tile_label_in)
                newState = Tile.STATE_ACTIVE

                info("State updated to active, starting timer...")
            } else {
                newLabel = getString(R.string.tile_label_out)
                newState = Tile.STATE_INACTIVE
                info("State updated to inactive, stopping timer...")
            }

            // Updating tile
            tile.updateTile(newState, newLabel)
        }
    }

    private fun stopInTimeUpdate() {
        info("Timer stopped")
        repeater.cancel()
        stopSelf()
    }

    private fun startInTimeUpdate() {

        info("Timer started")

        val not = NotificationCompat.Builder(this, App.CHANNEL_TIMER_ID)
            .setSmallIcon(R.drawable.ic_clock)
            .setContentTitle(getString(R.string.notif_title_live_timer))
            .setContentText(getString(R.string.notif_content_live_timer))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        startForeground(1324, not)


        // Update total in time in each second
        swipeRepository.getInSwipesTodayInMillis { _totalInSwipeInMs ->
            var totalInSwipeInMs = _totalInSwipeInMs
            repeater.cancel()
            repeater.startExecute {
                val toHHMM = DateUtils2.toHHmmss(totalInSwipeInMs)
                tile.updateTile(Tile.STATE_ACTIVE, toHHMM)
                info("Time update : $toHHMM: TaskID : ${repeater.toString().split("@").last()}")
                totalInSwipeInMs += 1000
            }
        }

    }

}
