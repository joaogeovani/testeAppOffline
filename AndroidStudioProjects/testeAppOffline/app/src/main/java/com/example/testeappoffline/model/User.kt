package com.example.testeappoffline.model

data class User(
    val id : String,
    var fullName : String,
    var region : String,
    val email : String,
    var imageURL: String? = null
)