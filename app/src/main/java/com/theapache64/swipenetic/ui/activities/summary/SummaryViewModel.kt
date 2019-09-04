package com.theapache64.swipenetic.ui.activities.summary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theapache64.swipenetic.data.repositories.SwipeRepository
import com.theapache64.swipenetic.models.SwipeSummary
import com.theapache64.twinkill.utils.DateUtils
import java.util.*
import javax.inject.Inject

class SummaryViewModel @Inject constructor(
    private val swipeRepository: SwipeRepository
) : ViewModel() {


    val toolbarSubtitle = MutableLiveData<String>()
    fun getToolbarSubtitle(): LiveData<String> = toolbarSubtitle

    lateinit var date: Date
    private val swipeSummary = MutableLiveData<List<SwipeSummary>>()
    fun getSwipeSummary(): LiveData<List<SwipeSummary>> = swipeSummary

    fun loadSwipeSummary(date: Date) {
        // finally
        this.date = date
        this.toolbarSubtitle.value = DateUtils.getReadableFormat(date)
        swipeRepository.getSwipeSummary(date) { summary ->
            this.swipeSummary.value = summary
        }
    }

}