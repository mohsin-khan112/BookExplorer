package com.example.acropolisassignment.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.acropolisassignment.db.dao.*
import com.example.acropolisassignment.db.entity.*

@Database(entities = [Genre::class, Book::class, User::class, Comment::class, FavouriteBook::class, FavouriteGenre::class, Note::class], version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun genreDAO(): GenreDAO

    abstract fun bookDAO(): BookDAO

    abstract fun userDAO(): UserDAO

    abstract fun commentDAO(): CommentDAO

    abstract fun favouriteBookDAO(): FavouriteBookDAO

    abstract fun favouriteGenreDAO(): FavouriteGenreDAO

    abstract fun noteDAO(): NoteDAO
}