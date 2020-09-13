package com.sabo.dominik.ontheroadalarm

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class AlarmRepository private constructor() {

    private object HOLDER {
        val INSTANCE = AlarmRepository()
    }

    companion object {
        val instance: AlarmRepository by lazy { HOLDER.INSTANCE }
    }

    val alarms: ArrayList<Alarm> = ArrayList<Alarm>(0)

    fun add(alarm: Alarm) {
        alarms.add(alarm)
    }

    fun remove(index: Int) {
        alarms.removeAt(index)
    }

    fun saveData(context: Context) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("alarms",
            AppCompatActivity.MODE_PRIVATE
        )
        val gson = Gson()
        val alarmsJson = gson.toJson(alarms)
        with(sharedPreferences.edit()) {
            putString("alarms", alarmsJson)
            commit()
        }
    }

    fun loadData(context: Context) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("alarms",
            AppCompatActivity.MODE_PRIVATE
        )
        val gson = Gson()
        val alarmsJson = sharedPreferences.getString("alarms", null) ?: return
        val alarmsType: Type = object : TypeToken<ArrayList<Alarm>>() {}.type
        alarms.addAll(gson.fromJson(alarmsJson, alarmsType))
    }

}