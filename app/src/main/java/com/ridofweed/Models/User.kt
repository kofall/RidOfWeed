package com.ridofweed.Models

class User {
    lateinit var username: String
    lateinit var email: String
    lateinit var password: String

    constructor(){}
    constructor(username: String, email: String, password: String) {
        this.username = username
        this.email = email
        this.password = password
    }

    constructor(parameters: List<String>) {
        this.username = parameters[0]
        this.email = parameters[1]
        this.password = parameters[2]
    }
}