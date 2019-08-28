package com.theapache64.swipenetic.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.theapache64.swipenetic.data.local.dao.SwipeDao
import com.theapache64.swipenetic.data.local.entities.Swipe
import com.theapache64.swipenetic.data.local.typeconverters.DateConverter
import com.theapache64.swipenetic.data.local.typeconverters.SwipeTagConverter
import com.theapache64.swipenetic.data.local.typeconverters.SwipeTypeConverter

@Database(
    entities = [Swipe::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    DateConverter::class,
    SwipeTypeConverter::class,
    SwipeTagConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun swipeDao(): SwipeDao
}