package com.example.modoomap.model

data class Posting(
    val id: String,
    val profileImg: String,
    val username: String,
    val title: String,
    val content: String,
    val commentCount: Long,
    val createdAt: Long,
)
