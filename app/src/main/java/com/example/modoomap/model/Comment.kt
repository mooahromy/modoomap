package com.example.modoomap.model

data class Comment(
    val id: String,
    val postingId: String,
    val content: String,
    val username: String,
    val email: String,
    val userProfile: String,
    val createdAt: Long,
)
