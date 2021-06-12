package com.example.acropolisassignment.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.acropolisassignment.db.entity.Note

@Dao
interface NoteDAO {

    @Query("SELECT * FROM NOTE WHERE uid = :uid AND bookId = :bookId")
    fun getNotes(uid: Int, bookId: Int): List<Note>

    @Insert
    fun addNote(note: Note): Long
}