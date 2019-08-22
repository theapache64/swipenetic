package com.theapache64.swipenetic.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.theapache64.swipenetic.data.local.entities.Swipe

@Dao
interface SwipeDao {

    @Insert
    fun insert(swipe: Swipe)

    @Query("SELECT * FROM swipes WHERE strftime('%Y-%m-%d', timestamp / 1000, 'unixepoch') = date('now')  ORDER BY id DESC LIMIT 1")
    fun getLastSwipeToday(): Swipe?

    @Query("SELECT * FROM swipes WHERE strftime('%Y-%m-%d', timestamp / 1000, 'unixepoch') = date('now')")
    fun getSwipesToday(): List<Swipe>

    @Query("SELECT * FROM swipes WHERE strftime('%d-%m-%Y', timestamp / 1000, 'unixepoch') = :date")
    fun getSwipes(date: String): List<Swipe>


}
