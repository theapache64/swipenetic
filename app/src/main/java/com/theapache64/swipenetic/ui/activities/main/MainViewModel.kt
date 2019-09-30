package com.theapache64.swipenetic.ui.activities.main


import android.app.Application
import android.text.format.DateUtils
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.work.WorkManager
import com.theapache64.swipenetic.data.local.entities.Swipe
import com.theapache64.swipenetic.data.repositories.GeneralPrefRepository
import com.theapache64.swipenetic.data.repositories.SwipeRepository
import com.theapache64.swipenetic.models.SwipeOutTag
import com.theapache64.swipenetic.utils.DateUtils2
import com.theapache64.swipenetic.utils.Repeater
import com.theapache64.swipenetic.utils.SwipeAlertManager
import com.theapache64.twinkill.logger.info
import java.util.*
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val swipeRepository: SwipeRepository,
    private val generalPrefRepository: GeneralPrefRepository,
    private val swipeAlertManager: SwipeAlertManager,
    application: Application
) : AndroidViewModel(application) {


    private var selectableDates: Array<Calendar>? = null

    val currentDate = MutableLiveData<Calendar>()
    private val swipeSessions = Transformations.switchMap(currentDate) { date ->
        swipeRepository.getSwipeSessions(date.time)
    }

    val isNextDateAvailable = ObservableBoolean(false)
    val totalInSwipe = ObservableField("00:00:00")
    private val swipeTimeRepeater = Repeater(1000)

    init {
        swipeRepository.getSelectableDates { dates ->
            val calList = mutableListOf<Calendar>()
            for (date in dates) {
                calList.add(DateUtils2.fromDDMMYYY(date))
            }
            this.selectableDates = calList.toTypedArray()
        }
    }

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
    fun changeDate(newDate: Calendar) {
        this.currentDate.value = newDate
        if (DateUtils.isToday(newDate.time.time) || newDate.after(Date())) {
            this.isNextDateAvailable.set(false)
        } else {
            this.isNextDateAvailable.set(true)
        }
    }

    fun changeDate() {
        val newDate = Calendar.getInstance().apply {
            val newTime = currentDate.value ?: Calendar.getInstance()
            time = newTime.time
        }
        changeDate(newDate)
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

    fun resetWorkAlert(swipeOutTag: SwipeOutTag) {

        // cancelling current work
        val alertWorkId = generalPrefRepository.getWorkId()
        val workManager = WorkManager.getInstance(getApplication())
        workManager.cancelWorkById(UUID.fromString(alertWorkId))

        // creating new work alert
        swipeAlertManager.scheduleAlert(getApplication(), swipeOutTag)
    }

    fun changeDateToPrev() {
        addDate(-1)
    }

    private fun addDate(dayCount: Int) {
        val newTime = currentDate.value ?: Calendar.getInstance()
        newTime.add(Calendar.DATE, dayCount)
        changeDate(newTime)
    }

    fun hasSwipeSessions(): Boolean {
        return swipeSessions.value?.data?.isNotEmpty() ?: false
    }

    fun resetInTimeToZero() {
        this.totalInSwipe.set("00:00:00")
    }

    fun changeDateToNext() {
        addDate(1)
    }

    fun getSelectableDates(): Array<Calendar>? {
        return selectableDates
    }
}