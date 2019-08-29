package com.theapache64.swipenetic.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.theapache64.swipenetic.R
import com.theapache64.swipenetic.data.local.dao.SwipeDao
import com.theapache64.swipenetic.data.local.entities.Swipe
import com.theapache64.swipenetic.models.SwipeSession
import com.theapache64.swipenetic.models.SwipeSummary
import com.theapache64.swipenetic.models.SwipeTag
import com.theapache64.swipenetic.utils.DateUtils2
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
    fun getTotalInSwipesInMillis(swipes: List<Swipe>): Long {
        when {
            swipes.isEmpty() -> return 0
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
            val ddmmYYYYdate = DateUtils.toDDMMYYY(date.time)
            val swipes = swipeDao.getSwipes(ddmmYYYYdate)
            val sessions = getSwipeSessionsFromSwipes(swipes)
            if (sessions.isNotEmpty()) {
                swipeSessions.postValue(Resource.success(sessions))
            } else {
                swipeSessions.postValue(Resource.error("No swipe found for $ddmmYYYYdate"))
            }
        }

        return swipeSessions
    }

    private fun getSwipeSessionsFromSwipes(swipes: List<Swipe>): List<SwipeSession> {

        val swipeSessions = mutableListOf<SwipeSession>()

        for (i in 0 until swipes.size step 2) {

            val inSwipe = swipes[i]
            val outSwipeIndex = i + 1

            if (outSwipeIndex < swipes.size) {

                val outSwipe = swipes[outSwipeIndex]

                // In Swipe
                swipeSessions.add(
                    SwipeSession(
                        Swipe.Type.IN,
                        outSwipe.timestamp.time - inSwipe.timestamp.time,
                        null,
                        DateUtils2.tohmma(inSwipe.timestamp),
                        DateUtils2.tohmma(outSwipe.timestamp),
                        inSwipe,
                        outSwipe
                    )
                )

                val inSwipeTwoIndex = i + 2
                if (inSwipeTwoIndex < swipes.size) {
                    val inSwipeTwo = swipes[inSwipeTwoIndex]

                    // Out swipe
                    swipeSessions.add(
                        SwipeSession(
                            Swipe.Type.OUT,
                            inSwipeTwo.timestamp.time - outSwipe.timestamp.time,
                            outSwipe.tag,
                            DateUtils2.tohmma(outSwipe.timestamp),
                            DateUtils2.tohmma(inSwipeTwo.timestamp),
                            outSwipe,
                            inSwipeTwo
                        )
                    )
                } else {
                    // Start out swipe session
                    val currentTime = Date()
                    swipeSessions.add(
                        SwipeSession(
                            Swipe.Type.OUT,
                            currentTime.time - outSwipe.timestamp.time,
                            outSwipe.tag,
                            DateUtils2.tohmma(outSwipe.timestamp),
                            DateUtils2.tohmma(currentTime),
                            outSwipe,
                            null
                        )
                    )
                }

            } else {

                // in swipe with current time
                val now = Date()
                swipeSessions.add(
                    SwipeSession(
                        Swipe.Type.IN,
                        now.time - inSwipe.timestamp.time,
                        null,
                        DateUtils2.tohmma(inSwipe.timestamp),
                        DateUtils2.tohmma(now),
                        inSwipe,
                        null
                    )
                )
            }
        }

        return swipeSessions.reversed()

    }

    fun getAllIds(): LiveData<Int> {
        return swipeDao.getTotalRows()
    }

    fun update(lastSwipe: Swipe) {
        appExecutors.diskIO().execute {
            swipeDao.updateSwipe(lastSwipe)
        }
    }

    fun getSwipesToday(callback: (List<Swipe>) -> Unit) {
        appExecutors.diskIO().execute {
            val swipes = swipeDao.getSwipesToday()
            appExecutors.mainThread().execute {
                callback(swipes)
            }
        }
    }

    fun getSwipeSummaryToday(callback: (List<SwipeSummary>) -> Unit) {

        val swipeSummary = mutableListOf<SwipeSummary>()

        appExecutors.diskIO().execute {

            val todaySwipes = swipeDao.getSwipesToday()


            // Calculating total time spent in office
            if (todaySwipes.isNotEmpty()) {

                // Calculating time spent
                val firstIn = todaySwipes.first().timestamp
                val lastOut =
                    if (todaySwipes.size == 1 || todaySwipes.last().type == Swipe.Type.IN) {
                        Date()
                    } else {
                        todaySwipes.last().timestamp
                    }

                val totalSwipeInMs = lastOut.time - firstIn.time

                // Adding time spent
                swipeSummary.add(
                    SwipeSummary(
                        R.drawable.ic_clock,
                        R.string.label_time_spent,
                        DateUtils2.getDuration(totalSwipeInMs)
                    )
                )

                val inSwipeInMs = getTotalInSwipesInMillis(todaySwipes)

                // Adding in swipe
                swipeSummary.add(
                    SwipeSummary(
                        R.drawable.ic_clock,
                        R.string.label_total_in_time,
                        DateUtils2.getDuration(inSwipeInMs)
                    )
                )

                // Calculating out swipe
                val totalOutSwipeInMs = totalSwipeInMs - inSwipeInMs

                // Adding out swipe
                swipeSummary.add(
                    SwipeSummary(
                        R.drawable.ic_clock,
                        R.string.label_total_out_time,
                        DateUtils2.getDuration(totalOutSwipeInMs)
                    )
                )

                val ttTimeInMs = getTotalOutTimeSpent(todaySwipes)

                appExecutors.mainThread().execute {
                    callback(swipeSummary)
                }
            }


        }

    }

    private fun getTotalOutTimeSpent(swipes: List<Swipe>): Map<SwipeTag, Long> {
        val map = mutableMapOf<SwipeTag, Long>()
        return map
    }


}