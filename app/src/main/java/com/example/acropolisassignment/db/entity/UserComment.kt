package com.example.acropolisassignment.db.entity

import androidx.room.Embedded

class UserComment {

    @Embedded
    var comment: Comment? = null

    @Embedded
    var user: User? = null
}