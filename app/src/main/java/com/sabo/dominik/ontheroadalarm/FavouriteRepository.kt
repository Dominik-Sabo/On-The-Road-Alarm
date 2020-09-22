package com.sabo.dominik.ontheroadalarm

import com.sabo.dominik.ontheroadalarm.models.FavouritePlace

class FavouriteRepository private constructor() {

    private object HOLDER {
        val INSTANCE = FavouriteRepository()
    }

    companion object {
        val instance: FavouriteRepository by lazy { HOLDER.INSTANCE }
    }

    val favourites: ArrayList<FavouritePlace> = ArrayList<FavouritePlace>(0)

    fun add(favouritePlace: FavouritePlace) {
        favourites.add(favouritePlace)
    }

    fun remove(index: Int) {
        favourites.removeAt(index)
    }
}