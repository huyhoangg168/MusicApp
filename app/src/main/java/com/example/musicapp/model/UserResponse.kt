package com.example.musicapp.model

import com.example.musicapp.model.User

data class UserResponse(
    val success: Boolean,
    val user: User?
)