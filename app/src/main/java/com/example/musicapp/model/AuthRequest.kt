package com.example.musicapp.model

data class AuthRequest(
    val uid: String,
    val email: String,
    val username: String?,
    val avatar_url: String?
)