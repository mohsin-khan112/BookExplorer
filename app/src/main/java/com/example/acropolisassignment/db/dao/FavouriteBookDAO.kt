package com.example.acropolisassignment.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.acropolisassignment.db.entity.FavouriteBook

@Dao
interface FavouriteBookDAO {

    @Query("SELECT * FROM FavouriteBook WHERE user_id = :userId AND book_id = :bookId")
    fun isFavouriteBook(userId: Int, bookId: Int): FavouriteBook?

    @Insert
    fun addToFavouriteBook(favouriteBook: FavouriteBook): Long

    @Delete
    fun deleteFromFavouriteBook(favouriteBook: FavouriteBook): Int
}