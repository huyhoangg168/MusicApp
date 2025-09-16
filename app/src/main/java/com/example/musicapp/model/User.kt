package com.example.musicapp.model

data class User(
    val id: Int,
    val firebase_uid: String,
    val email: String,
    val username: String?,
    val avatar_url: String?
)