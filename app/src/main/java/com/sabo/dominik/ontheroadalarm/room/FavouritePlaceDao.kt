package com.sabo.dominik.ontheroadalarm.room

import androidx.room.*
import com.sabo.dominik.ontheroadalarm.models.FavouritePlace

@Dao
interface FavouritePlaceDao {
    @Insert
    fun insertFavPlace(alarm: FavouritePlace): Long

    @Query("SELECT * FROM FavouritePlace")
    fun getFavPlaces(): List<FavouritePlace>

    @Query("SELECT * FROM FavouritePlace where id = :id")
    fun getFavPlace(id: Int): FavouritePlace

    @Delete
    fun deleteFavPlace(favPlace: FavouritePlace)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateFavPlace(favPlace: FavouritePlace)
}