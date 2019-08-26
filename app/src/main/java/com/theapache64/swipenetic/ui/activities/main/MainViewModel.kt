package com.theapache64.swipenetic.ui.activities.main


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.theapache64.swipenetic.data.repositories.SwipeRepository
import java.util.*
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val swipeRepository: SwipeRepository
) : ViewModel() {

    val currentDate = MutableLiveData<Calendar>(Calendar.getInstance())
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
            time = currentDate.value!!.time
        }
    }


    fun getSwipeSessions() = swipeSessions
}