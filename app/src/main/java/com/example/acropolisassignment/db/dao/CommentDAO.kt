package com.example.acropolisassignment.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.acropolisassignment.db.entity.Comment
import com.example.acropolisassignment.db.entity.UserComment

@Dao
interface CommentDAO {

    @Query("SELECT COMMENT.*, USER.* FROM COMMENT INNER JOIN USER ON user_id = uid WHERE book_id = :bookId")
    fun getComments(bookId: Int): List<UserComment>

    @Query("SELECT COMMENT.*, USER.* FROM COMMENT INNER JOIN USER ON user_id = uid WHERE comment_id = :commentId")
    fun getCommentById(commentId: Int): UserComment

    @Insert
    fun addComment(comment: Comment): Long
}