package com.sabo.dominik.ontheroadalarm.repository

import android.app.Application
import com.sabo.dominik.ontheroadalarm.models.Alarm
import com.sabo.dominik.ontheroadalarm.room.OnTheRoadAlarmDatabase

class AlarmRepository private constructor() {

    private object HOLDER {
        val INSTANCE = AlarmRepository()
    }

    companion object {
        val instance: AlarmRepository by lazy { HOLDER.INSTANCE }
    }

    var alarms: ArrayList<Alarm> = ArrayList(0)

    fun add(alarm: Alarm, application: Application) {
        val database = OnTheRoadAlarmDatabase.get(application)
        database.run { alarm.id = database.getAlarmDao().insertAlarm(alarm) }
        alarms.add(alarm)
    }

    fun remove(index: Int, application: Application) {
        val database = OnTheRoadAlarmDatabase.get(application)
        database.run { database.getAlarmDao().deleteAlarm(alarms[index])}
        alarms.removeAt(index)
    }

    fun update(index: Int, alarm: Alarm, application: Application){
        alarm.id = alarms[index].id
        alarms[index] = alarm
        val database = OnTheRoadAlarmDatabase.get(application)
        database.run{database.getAlarmDao().updateAlarm(alarms[index])}
    }

    fun loadData(application: Application) {
        val database = OnTheRoadAlarmDatabase.get(application)
        database.run { alarms = database.getAlarmDao().getAlarms() as ArrayList<Alarm> }
    }

    fun toggle(position: Int, application: Application){
        alarms[position].toggle()
        val database = OnTheRoadAlarmDatabase.get(application)
        database.run {database.getAlarmDao().updateAlarm(alarms[position])}
    }

}