package com.theapache64.swipenetic.data.repositories

import com.theapache64.swipenetic.data.local.entities.Swipe
import com.theapache64.swipenetic.models.SwipeTag
import org.junit.Test
import java.util.*

class SwipeRepositoryTest {

    @Test
    fun test() {
        val swipes = listOf(
            Swipe(Date(1000), Swipe.Type.IN),
            Swipe(Date(2000), Swipe.Type.OUT),
            Swipe(Date(4000), Swipe.Type.IN),
            Swipe(Date(4000), Swipe.Type.OUT, SwipeTag.COFFEE),
            Swipe(Date(5000), Swipe.Type.IN)

        )

        val map = mutableMapOf<SwipeTag, Long>()

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
                    map[outSwipe.tag] = if (map[outSwipe.tag] == null) {
                        diff
                    } else {
                        map[outSwipe.tag]!! + diff
                    }
                } else {

                    // currently out
                    map[outSwipe.tag] = if (map[outSwipe.tag] == null) {
                        Date().time
                    } else {
                        map[outSwipe.tag]!! + Date().time
                    }
                }
            }
        }


        println("Total swipe is $map")
    }
}