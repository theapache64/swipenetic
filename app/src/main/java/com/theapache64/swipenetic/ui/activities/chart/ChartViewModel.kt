package com.theapache64.swipenetic.ui.activities.chart

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.PieEntry
import com.theapache64.swipenetic.data.repositories.SwipeRepository
import java.util.*
import javax.inject.Inject

class ChartViewModel @Inject constructor(
    private val swipeRepository: SwipeRepository
) : ViewModel() {

    private lateinit var date: Date
    fun init(date: Date) {
        this.date = date
    }

    private val chartData = MutableLiveData<List<PieEntry>>()
    fun getChartData() = chartData

    fun loadChartData() {

        // Getting in time
        swipeRepository.getS(date) { inSwipe ->
            swipeRepository.
        }

    }
}