package com.example.acropolisassignment.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
class Genre(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id")val id: Int = 0,
    @ColumnInfo val type: String? = null
): Serializable