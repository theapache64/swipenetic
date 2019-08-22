package com.theapache64.swipenetic.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.theapache64.swipenetic.data.local.dao.SwipeDao
import com.theapache64.swipenetic.data.local.entities.Swipe
import com.theapache64.swipenetic.models.SwipeSession
import com.theapache64.twinkill.utils.AppExecutors
import com.theapache64.twinkill.utils.DateUtils
import com.theapache64.twinkill.utils.Resource
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

    fun getInSwipesTodayInMillis(onInSwipesToday: (Long) -> Unit) {
        appExecutors.diskIO().execute {

            val swipesToday = swipeDao.getSwipesToday()
            val inSwipesInMillis = getTotalInSwipesInMillis(swipesToday)

            appExecutors.mainThread().execute {
                onInSwipesToday(inSwipesInMillis)
            }
        }
    }

    /**
     * To get in time in milliseconds from swipe list
     */
    private fun getTotalInSwipesInMillis(swipes: List<Swipe>): Long {
        when {
            swipes.isEmpty() -> throw IllegalArgumentException("No swipe data found")
            swipes.size == 1 -> return (System.currentTimeMillis() - swipes.first().timestamp.time)
            else -> {

                //swipes.size >= 2
                var totalSwipesInMs = 0L
                val isUserIn = swipes.size % 2 != 0
                val swipeSize = if (isUserIn) (swipes.size - 2) else (swipes.size - 1)

                for (i in 0..swipeSize step 2) {
                    val swipeIn = swipes[i]
                    val swipeOut = swipes[i + 1]
                    val inTime = swipeOut.timestamp.time - swipeIn.timestamp.time
                    totalSwipesInMs += inTime
                }

                if (isUserIn) {
                    val lastInSwipe = swipes.last()
                    totalSwipesInMs += (System.currentTimeMillis() - lastInSwipe.timestamp.time)
                }

                return totalSwipesInMs
            }
        }
    }


    fun getSwipeSessions(date: Calendar): LiveData<Resource<List<SwipeSession>>> {
        val swipeSessions = MutableLiveData<Resource<List<SwipeSession>>>()

        // loading
        swipeSessions.value = Resource.loading()
        appExecutors.diskIO().execute {
            val date = DateUtils.toDDMMYYY(date.time)
            val swipes = swipeDao.getSwipes(date)
            val swipeSessions = getSwipeSessionsFromSwipes(swipes)
        }

        return swipeSessions
    }

    private fun getSwipeSessionsFromSwipes(swipes: List<Swipe>): List<SwipeSession> {

        val swipeSessions = mutableListOf<SwipeSession>()
        val isUserIn = swipes.size % 2 != 0
        val swipeSize = if (isUserIn) (swipes.size - 2) else (swipes.size - 1)

        for (i in 0..swipeSize step 2) {
            val swipeIn = swipes[i]
            val swipeOut = swipes[i + 1]

            val swipeSession = getSwipeSessionFrom(swipeIn, swipeOut)
            swipeSessions.add(swipeSession)
        }

        return swipeSessions

    }

    private fun getSwipeSessionFrom(swipeIn: Swipe, swipeOut: Swipe): SwipeSession {
        return null!!
    }

}