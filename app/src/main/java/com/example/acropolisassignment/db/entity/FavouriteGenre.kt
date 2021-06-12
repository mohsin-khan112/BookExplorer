package com.example.acropolisassignment.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FavouriteGenre(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id") var id: Int = 0,

    @ColumnInfo(name = "genre_id") val genreId: Int,

    @ColumnInfo(name = "user_id") val userId: Int

)