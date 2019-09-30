package com.theapache64.swipenetic.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.theapache64.swipenetic.R
import com.theapache64.swipenetic.data.local.dao.SwipeDao
import com.theapache64.swipenetic.data.local.entities.Swipe
import com.theapache64.swipenetic.models.SwipeOutTag
import com.theapache64.swipenetic.models.SwipeSession
import com.theapache64.swipenetic.models.SwipeSummary
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

    fun getInSwipesTodayInMillis(callback: (Long) -> Unit) {
        getInSwipesInMillis(Date(), callback)
    }

    fun getInSwipesInMillis(date: Date, callback: (Long) -> Unit) {
        appExecutors.diskIO().execute {

            val dateFormatted = DateUtils.toDDMMYYY(date)
            val swipesToday = swipeDao.getSwipes(dateFormatted)
            val inSwipesInMillis = getTotalInSwipesInMillis(swipesToday)

            appExecutors.mainThread().execute {
                callback(inSwipesInMillis)
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


    fun getSwipeSessions(date: Date): LiveData<Resource<List<SwipeSession>>> {
        val swipeSessions = MutableLiveData<Resource<List<SwipeSession>>>()

        // loading
        swipeSessions.value = Resource.loading()
        appExecutors.diskIO().execute {
            val ddmmYYYYdate = DateUtils.toDDMMYYY(date)
            val swipes = swipeDao.getSwipes(ddmmYYYYdate)
            val sessions = getSwipeSessionsFromSwipes(date, swipes)
            if (sessions.isNotEmpty()) {
                swipeSessions.postValue(Resource.success(sessions))
            } else {
                swipeSessions.postValue(Resource.error("No swipe found for $ddmmYYYYdate"))
            }
        }

        return swipeSessions
    }

    private fun getSwipeSessionsFromSwipes(date: Date, swipes: List<Swipe>): List<SwipeSession> {

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
                            outSwipe.outTag,
                            DateUtils2.tohmma(outSwipe.timestamp),
                            DateUtils2.tohmma(inSwipeTwo.timestamp),
                            outSwipe,
                            inSwipeTwo
                        )
                    )
                } else {
                    // Start out swipe session
                    if (android.text.format.DateUtils.isToday(date.time)) {
                        val currentTime = Date()
                        swipeSessions.add(
                            SwipeSession(
                                Swipe.Type.OUT,
                                currentTime.time - outSwipe.timestamp.time,
                                outSwipe.outTag,
                                DateUtils2.tohmma(outSwipe.timestamp),
                                DateUtils2.tohmma(currentTime),
                                outSwipe,
                                null
                            )
                        )
                    }
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


    fun getSwipeSummary(date: Date, callback: (List<SwipeSummary>) -> Unit) {

        val swipeSummary = mutableListOf<SwipeSummary>()

        appExecutors.diskIO().execute {

            val dateFormatted = DateUtils.toDDMMYYY(date)
            val todaySwipes = swipeDao.getSwipes(dateFormatted)


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
                        "Total time spent",
                        DateUtils2.getDuration(totalSwipeInMs)
                    )
                )

                val inSwipeInMs = getTotalInSwipesInMillis(todaySwipes)

                // Adding in swipe
                swipeSummary.add(
                    SwipeSummary(
                        R.drawable.ic_clock,
                        "Total in time",
                        DateUtils2.getDuration(inSwipeInMs)

                    )
                )

                // Calculating out swipe
                val totalOutSwipeInMs = totalSwipeInMs - inSwipeInMs

                // Adding out swipe
                swipeSummary.add(
                    SwipeSummary(
                        R.drawable.ic_clock,
                        "Total out time",
                        DateUtils2.getDuration(totalOutSwipeInMs)
                    )
                )

                val tagsAndTimes = getSwipeTagWithTotalTimeSpent(todaySwipes)
                for (tagAndTime in tagsAndTimes) {

                    swipeSummary.add(
                        SwipeSummary(
                            tagAndTime.key.image,
                            tagAndTime.key.label,
                            DateUtils2.getDuration(tagAndTime.value)
                        )
                    )
                }

                appExecutors.mainThread().execute {
                    callback(swipeSummary)
                }
            }

        }

    }


    fun getSwipeTagWithTotalTimeSpent(swipes: List<Swipe>): Map<SwipeOutTag, Long> {

        val map = mutableMapOf<SwipeOutTag, Long>()

        for (i in 0 until swipes.size step 2) {

            val outSwipeIndex = i + 1
            if (outSwipeIndex < swipes.size) {

                val outSwipe = swipes[outSwipeIndex]

                if (outSwipe.type != Swipe.Type.OUT) {
                    throw IllegalArgumentException("Invalid swipe out data")
                }

                val inSwipe2Index = i + 2
                if (inSwipe2Index < swipes.size) {
                    // next in is available
                    val inSwipe2 = swipes[inSwipe2Index]
                    val diff = inSwipe2.timestamp.time - outSwipe.timestamp.time
                    map[outSwipe.outTag] = if (map[outSwipe.outTag] == null) {
                        diff
                    } else {
                        map[outSwipe.outTag]!! + diff
                    }
                } else {

                    // currently out
                    if (android.text.format.DateUtils.isToday(outSwipe.timestamp.time)) {
                        val diff = Date().time - outSwipe.timestamp.time
                        map[outSwipe.outTag] = if (map[outSwipe.outTag] == null) {
                            diff
                        } else {
                            map[outSwipe.outTag]!! + diff
                        }
                    }
                }
            }
        }

        return map
    }

    /**
     * To get in swipe time and out swipe times with theirs tags
     */
    fun getInTimeAndOutTags(date: Date, callback: (Long, Map<SwipeOutTag, Long>) -> Unit) {
        appExecutors.diskIO().execute {
            val dateFormatted = DateUtils.toDDMMYYY(date)
            val swipes = swipeDao.getSwipes(dateFormatted)
            val inSwipesInMillis = getTotalInSwipesInMillis(swipes)
            val outTags = getSwipeTagWithTotalTimeSpent(swipes)
            appExecutors.mainThread().execute {
                callback(inSwipesInMillis, outTags)
            }
        }
    }

    fun getSelectableDates(callback: (List<String>) -> Unit) {
        appExecutors.diskIO().execute {
            val dates = swipeDao.getAllDates()
            callback(dates)
        }
    }


}