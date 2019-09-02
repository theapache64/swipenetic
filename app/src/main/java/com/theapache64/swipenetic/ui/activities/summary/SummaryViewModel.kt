package com.theapache64.swipenetic.ui.activities.summary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theapache64.swipenetic.data.repositories.SwipeRepository
import com.theapache64.swipenetic.models.SwipeSummary
import java.util.*
import javax.inject.Inject

class SummaryViewModel @Inject constructor(
    private val swipeRepository: SwipeRepository
) : ViewModel() {

    private val swipeSummary = MutableLiveData<List<SwipeSummary>>()
    fun getSwipeSummary(): LiveData<List<SwipeSummary>> = swipeSummary


    fun loadSwipeSummary(date: Date) {
        // finally
        swipeRepository.getSwipeSummary(date) { summary ->
            this.swipeSummary.value = summary
        }
    }

    fun filterInSwipe(callback: (Long) -> Unit) {
        swipeRepository.getInSwipesTodayInMillis { swipeInMs ->
            callback(swipeInMs)
        }
    }


}