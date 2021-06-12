package com.example.acropolisassignment.db.entity

import androidx.room.*
import java.io.Serializable

@Entity(
    foreignKeys = [
        ForeignKey(entity = Genre::class, parentColumns = ["_id"], childColumns = ["genre_id"])
    ]
)
class Book(

    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo val name: String,
    @ColumnInfo val description: String,
    @ColumnInfo val photo: String, //base64 image
    @ColumnInfo(name = "genre_id") val genreId: Int
): Serializable
