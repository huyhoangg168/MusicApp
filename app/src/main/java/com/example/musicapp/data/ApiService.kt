package com.example.musicapp.data

import com.example.musicapp.model.AuthRequest
import com.example.musicapp.model.UserResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("auth/firebase")
    suspend fun authWithFirebase(@Body request: AuthRequest): UserResponse
}