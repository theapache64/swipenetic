package com.theapache64.swipenetic.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.theapache64.swipenetic.data.local.entities.Swipe

@Dao
interface SwipeDao {

    @Insert
    fun insert(swipe: Swipe)

    @Query("SELECT * FROM swipes WHERE strftime('%Y-%m-%d', timestamp / 1000, 'unixepoch') = date('now')  ORDER BY id DESC LIMIT 1")
    fun getLastSwipeToday(): Swipe?

    @Query("SELECT * FROM swipes WHERE strftime('%d-%m-%Y', timestamp / 1000, 'unixepoch') = :date")
    fun getSwipes(date: String): List<Swipe>

    @Query("SELECT COUNT(id) FROM swipes WHERE strftime('%Y-%m-%d', timestamp / 1000, 'unixepoch') = date('now')")
    fun getTotalRows(): LiveData<Int>

    @Update
    fun updateSwipe(swipe: Swipe)

    @Query("SELECT strftime('%d-%m-%Y', timestamp / 1000, 'unixepoch') as dates FROM swipes GROUP BY dates;")
    fun getAllDates(): List<String>
}
