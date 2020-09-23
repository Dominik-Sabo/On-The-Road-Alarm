package com.sabo.dominik.ontheroadalarm.room

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sabo.dominik.ontheroadalarm.models.Alarm
import com.sabo.dominik.ontheroadalarm.models.FavouritePlace

@Database(version = 1, entities = [Alarm::class, FavouritePlace::class])
abstract class OnTheRoadAlarmDatabase : RoomDatabase() {
    companion object{
        fun get(application: Application) : OnTheRoadAlarmDatabase{
            return Room.databaseBuilder(application, OnTheRoadAlarmDatabase::class.java, "OnTheRoadAlarmDB").allowMainThreadQueries().build()
        }
    }
    abstract fun getAlarmDao(): AlarmDao
    abstract fun getFavouritePlaceDao(): FavouritePlaceDao
}