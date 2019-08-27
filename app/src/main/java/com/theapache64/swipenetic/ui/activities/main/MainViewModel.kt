package com.theapache64.swipenetic.ui.activities.main


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.theapache64.swipenetic.data.repositories.SwipeRepository
import java.util.*
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val swipeRepository: SwipeRepository
) : ViewModel() {

    val currentDate = MutableLiveData<Calendar>()
    private val swipeSessions = Transformations.switchMap(currentDate) { date ->
        swipeRepository.getSwipeSessions(date)
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
}