package com.theapache64.swipenetic.data.repositories

import com.theapache64.swipenetic.data.local.dao.SwipeDao
import com.theapache64.swipenetic.data.local.entities.Swipe
import com.theapache64.twinkill.utils.AppExecutors
import java.util.*
import javax.inject.Inject

class SwipeRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val swipeDao: SwipeDao
) {

    fun insertSwipe(type: Swipe.Type) {
        appExecutors.diskIO().execute {
            swipeDao.insert(Swipe(Calendar.getInstance().time, type))
        }
    }


    fun getLastSwipeToday(onSwipeToday: (Swipe?) -> Unit) {
        appExecutors.diskIO().execute {
            val lastSwipe = swipeDao.getLastSwipeToday()
            appExecutors.mainThread().execute {
                onSwipeToday(lastSwipe)
            }
        }
    }

}