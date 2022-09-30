package com.ridofweed.Models

import com.google.firebase.database.ServerValue

class Post {
    lateinit var postKey: String
    lateinit var username: String
    lateinit var title: String
    lateinit var description: String
    lateinit var picture: String
    lateinit var timeStamp: String

    constructor(){}
    constructor(
        username: String,
        title: String,
        description: String,
        picture: String,
        timeStamp: String? = null
    ) {
        this.username = username
        this.title = title
        this.description = description
        this.picture = picture
        this.timeStamp = System.currentTimeMillis().toString()
        if (timeStamp != null) {
            this.timeStamp = timeStamp
        }
    }
}