package com.sabo.dominik.ontheroadalarm.room

import androidx.room.*
import com.sabo.dominik.ontheroadalarm.models.Alarm

@Dao
interface AlarmDao {

    @Insert
    fun insertAlarm(alarm: Alarm): Long

    @Query("SELECT * FROM Alarm")
    fun getAlarms(): List<Alarm>

    @Query("SELECT * FROM Alarm where id = :id")
    fun getAlarm(id: Int): Alarm

    @Delete
    fun deleteAlarm(alarm: Alarm)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateAlarm(alarm: Alarm)
}