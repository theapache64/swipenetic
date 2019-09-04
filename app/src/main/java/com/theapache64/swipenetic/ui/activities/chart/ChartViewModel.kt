package com.theapache64.swipenetic.ui.activities.chart

import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.theapache64.quickpercent.isWhatPercentOf
import com.theapache64.swipenetic.data.repositories.SwipeRepository
import com.theapache64.swipenetic.models.SwipeTag
import com.theapache64.twinkill.logger.info
import java.util.*
import javax.inject.Inject

class ChartViewModel @Inject constructor(
    private val swipeRepository: SwipeRepository
) : ViewModel() {

    private lateinit var date: Date
    fun init(date: Date) {
        this.date = date
    }

    private val chartData = MutableLiveData<PieData>()
    fun getChartData() = chartData

    fun loadChartData() {

        // Getting in time
        swipeRepository.getInTimeAndOutTags(date) { inTime: Long, outTags: Map<SwipeTag, Long> ->
            info("InTime is ${inTime}")
            info("OutTags are $outTags")
            val totalTime = inTime + outTags.values.sum()
            val inTimePerc = inTime.isWhatPercentOf(totalTime)


            val entries = mutableListOf<PieEntry>()
            entries.add(PieEntry(inTimePerc, "In Time"))
            for (outTag in outTags) {
                val outPerc = outTag.value.isWhatPercentOf(totalTime)
                entries.add(PieEntry(outPerc, outTag.key.label))
            }

            val dataSet = PieDataSet(entries, "")
            dataSet.colors = ColorTemplate.MATERIAL_COLORS.toMutableList()
            val pieData = PieData(dataSet)
            pieData.setValueTextColor(Color.BLACK)
            pieData.setValueTextSize(20f)

            chartData.value = pieData
        }

    }
}