package com.example.acropolisassignment.db.entity

import androidx.room.*

@Entity(
    foreignKeys = [
        ForeignKey(entity = User::class, parentColumns = ["uid"], childColumns = ["user_id"])
    ]
)
class Comment(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "comment_id") val commentId: Int = 0,
    val text: String,
    @ColumnInfo(name = "book_id") val bookId: Int,
    @ColumnInfo(name = "user_id") val userId: Int

)