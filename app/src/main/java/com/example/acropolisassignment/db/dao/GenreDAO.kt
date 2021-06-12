package com.example.acropolisassignment.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.acropolisassignment.db.entity.Genre

@Dao
interface GenreDAO {

    @Query("SELECT * FROM genre")
    fun getAllGenre(): List<Genre>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(genreList: List<Genre>)
}