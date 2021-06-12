package com.example.acropolisassignment.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class FavouriteBook (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id") var id: Int = 0,

    @ColumnInfo(name = "book_id") val bookId: Int,

    @ColumnInfo(name = "user_id") val userId: Int
)