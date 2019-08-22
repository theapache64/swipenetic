package com.theapache64.swipenetic.data.repositories

import com.theapache64.swipenetic.data.local.entities.Swipe
import com.theapache64.swipenetic.models.SwipeSession
import org.junit.Test
import java.util.*

class SwipeRepositoryTest {

    @Test
    fun test() {
        val swipes = listOf(
            Swipe(Date(1000), Swipe.Type.IN),
            Swipe(Date(2000), Swipe.Type.OUT),
            Swipe(Date(3000), Swipe.Type.IN),

            Swipe(Date(4000), Swipe.Type.OUT),
            Swipe(Date(5000), Swipe.Type.IN),
            Swipe(Date(6000), Swipe.Type.OUT),

            Swipe(Date(7000), Swipe.Type.IN)

        )

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
                        "${outSwipe.timestamp.time - inSwipe.timestamp.time}",
                        null,
                        "${inSwipe.timestamp.time}",
                        "${outSwipe.timestamp.time}"
                    )
                )

                val inSwipeTwoIndex = i + 2
                if (inSwipeTwoIndex < swipes.size) {
                    val inSwipeTwo = swipes[inSwipeTwoIndex]

                    // Out swipe
                    swipeSessions.add(
                        SwipeSession(
                            Swipe.Type.OUT,
                            "${inSwipeTwo.timestamp.time - outSwipe.timestamp.time}",
                            null,
                            "${outSwipe.timestamp.time}",
                            "${inSwipeTwo.timestamp.time}"


                        )
                    )
                }
            } else {
                // in swipe with current time
                val currentTime = 9000
                swipeSessions.add(
                    SwipeSession(
                        Swipe.Type.IN,
                        "${currentTime - inSwipe.timestamp.time}",
                        null,
                        "${inSwipe.timestamp.time}",
                        "$currentTime"
                    )
                )
            }
        }

        for (swipeSession in swipeSessions) {
            println(swipeSession)
        }


    }
}