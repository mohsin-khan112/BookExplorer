package com.example.acropolisassignment.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.acropolisassignment.db.entity.FavouriteGenre

@Dao
interface FavouriteGenreDAO {

    @Query("SELECT * FROM FAVOURITEGENRE WHERE user_id = :userId AND genre_id = :genreId")
    fun isFavouriteGenre(userId: Int, genreId: Int): FavouriteGenre?

    @Insert
    fun addToFavouriteGenre(favouriteGenre: FavouriteGenre): Long

    @Delete
    fun deleteFromFavouriteGenre(favouriteGenre: FavouriteGenre): Int
}