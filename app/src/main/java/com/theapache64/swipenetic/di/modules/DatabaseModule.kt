package com.theapache64.swipenetic.di.modules

import android.app.Application
import androidx.room.Room
import com.theapache64.swipenetic.data.local.AppDatabase
import com.theapache64.swipenetic.data.local.dao.SwipeDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(app, AppDatabase::class.java, "swipenetic.db")
            .build()
    }

    @Singleton
    @Provides
    fun provideSwipeDao(appDatabase: AppDatabase): SwipeDao {
        return appDatabase.swipeDao()
    }

}