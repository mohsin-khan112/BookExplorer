package com.example.acropolisassignment.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class User (
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String
)