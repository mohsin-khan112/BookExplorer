package com.example.acropolisassignment.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.acropolisassignment.db.entity.Book
import com.example.acropolisassignment.db.entity.GenreBook

@Dao
interface BookDAO {

    @Query("Select Book.*, Genre.* From Book INNER JOIN Genre ON Book.genre_id = Genre._id where genre_id = :genreId")
    fun getBooksByGenre(genreId: Int): List<GenreBook>

    @Query("Select Book.*, Genre.* From Book INNER JOIN Genre ON Book.genre_id = Genre._id where id = :bookId")
    fun getBookInfo(bookId: Int): GenreBook

    @Query("Select Book.*, Genre.* From Book INNER JOIN Genre ON Book.genre_id = Genre._id where name LIKE :query")
    fun getBooksByName(query: String): List<GenreBook>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllBooks(books: List<Book>)

    @Insert
    fun addBook(book: Book): Long
}