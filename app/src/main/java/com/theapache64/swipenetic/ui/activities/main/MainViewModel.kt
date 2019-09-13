package com.theapache64.swipenetic.ui.activities.main


import android.text.format.DateUtils
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.theapache64.swipenetic.data.local.entities.Swipe
import com.theapache64.swipenetic.data.repositories.SwipeRepository
import com.theapache64.swipenetic.utils.DateUtils2
import com.theapache64.swipenetic.utils.Repeater
import com.theapache64.twinkill.logger.info
import java.util.*
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val swipeRepository: SwipeRepository
) : ViewModel() {

    val currentDate = MutableLiveData<Calendar>()
    private val swipeSessions = Transformations.switchMap(currentDate) { date ->
        swipeRepository.getSwipeSessions(date.time)
    }

    val totalInSwipe = ObservableField("00:00:00")


    private val swipeTimeRepeater = Repeater(1000)

    fun checkAndStartTotalInSwipeCounting() {

        info("checkAndStartTotalInSwipeCounting call")

        swipeRepository.getInSwipesInMillis(currentDate.value?.time!!) { _inSwipeInMillis ->

            var inSwipeInMillis = _inSwipeInMillis
            var totalInTime = DateUtils2.toHHmmss(inSwipeInMillis)
            totalInSwipe.set(totalInTime)

            // checking if the current date is today
            if (DateUtils.isToday(currentDate.value?.time?.time ?: 0)) {

                // checking if the user is in or not
                swipeRepository.getLastSwipeToday { lastSwipe ->
                    if (lastSwipe != null && lastSwipe.type == Swipe.Type.IN) {
                        // user is in, so start count down
                        swipeTimeRepeater.cancel()
                        swipeTimeRepeater.startExecute {
                            inSwipeInMillis += 1000
                            totalInTime = DateUtils2.toHHmmss(inSwipeInMillis)
                            totalInSwipe.set(totalInTime)
                        }
                    } else {
                        swipeTimeRepeater.cancel()
                    }
                }
            } else {
                swipeTimeRepeater.cancel()
            }
        }


    }

    /**
     * Sets new date
     */
    fun loadSwipeSessions(newDate: Calendar) {
        this.currentDate.value = newDate
    }

    fun loadSwipeSessions() {
        this.currentDate.value = Calendar.getInstance().apply {
            val newTime = currentDate.value ?: Calendar.getInstance()
            time = newTime.time
        }
    }


    fun getSwipeSessions() = swipeSessions
    fun getSwipeChange(): LiveData<Int> {
        return swipeRepository.getAllIds()
    }

    fun updateSwipe(swipe: Swipe) {
        swipeRepository.update(swipe)
    }

    fun getCurrentDate(): Date {
        return currentDate.value?.time ?: Calendar.getInstance().time
    }

    fun getCurrentDateLiveData() = currentDate
}