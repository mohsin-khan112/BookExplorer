package com.example.acropolisassignment.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.acropolisassignment.db.entity.User

@Dao
interface UserDAO {

    @Query("SELECT * FROM USER WHERE EMAIL = :email AND PASSWORD = :password")
    fun signIn(email: String, password: String): User?

    @Insert
    fun insertUser(user: User): Long
}