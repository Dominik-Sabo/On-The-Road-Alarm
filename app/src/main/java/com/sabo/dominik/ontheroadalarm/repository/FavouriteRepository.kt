package com.sabo.dominik.ontheroadalarm.repository

import android.app.Application
import com.sabo.dominik.ontheroadalarm.models.FavouritePlace
import com.sabo.dominik.ontheroadalarm.room.OnTheRoadAlarmDatabase

class FavouriteRepository private constructor() {

    private object HOLDER {
        val INSTANCE = FavouriteRepository()
    }

    companion object {
        val instance: FavouriteRepository by lazy { HOLDER.INSTANCE }
    }

    var favourites: ArrayList<FavouritePlace> = ArrayList<FavouritePlace>(0)

    fun add(favouritePlace: FavouritePlace, application: Application) {
        favourites.add(favouritePlace)
        val database = OnTheRoadAlarmDatabase.get(application)
        database.run { database.getFavouritePlaceDao().insertFavPlace(favouritePlace) }
    }

    fun remove(index: Int, application: Application) {
        val database = OnTheRoadAlarmDatabase.get(application)
        database.run { database.getFavouritePlaceDao().deleteFavPlace(favourites[index])}
        favourites.removeAt(index)
    }

    fun update(index: Int, favouritePlace: FavouritePlace, application: Application){
        favouritePlace.id = favourites[index].id
        favourites[index] = favouritePlace
        val database = OnTheRoadAlarmDatabase.get(application)
        database.run{database.getFavouritePlaceDao().updateFavPlace(favourites[index])}
    }

    fun loadData(application: Application) {
        val database = OnTheRoadAlarmDatabase.get(application)
        database.run { favourites = database.getFavouritePlaceDao().getFavPlaces() as ArrayList<FavouritePlace> }
    }
}