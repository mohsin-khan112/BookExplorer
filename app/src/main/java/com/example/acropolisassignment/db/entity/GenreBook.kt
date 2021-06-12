package com.example.acropolisassignment.db.entity

import androidx.room.Embedded
import androidx.room.Relation
import java.io.Serializable

class GenreBook: Serializable {

    @Embedded
    var genre: Genre? = null

    @Embedded
    var book: Book? = null
}