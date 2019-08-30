package com.theapache64.swipenetic.ui.activities.chart

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieEntry
import com.theapache64.swipenetic.models.SwipeTag
import javax.inject.Inject

class ChartViewModel @Inject constructor() : ViewModel() {

    private lateinit var outSwipe: Map<SwipeTag, Long>
    private var inSwipe: Long = 0

    fun init(inSwipe: Long, outSwipe: Map<SwipeTag, Long>) {
        this.inSwipe = inSwipe
        this.outSwipe = outSwipe
    }

    private val chartData = MutableLiveData<List<PieEntry>>()
    fun getChartData() = chartData

    fun loadChartData() {


        val entries = mutableListOf<PieEntry>()
        val outSwipe = outSwipe.values.sum()

        entries.add(PieEntry(inSwipe.toFloat(), 0f))
        entries.add(PieEntry(outSwipe.toFloat(), 1f))

        chartData.value = entries
    }
}