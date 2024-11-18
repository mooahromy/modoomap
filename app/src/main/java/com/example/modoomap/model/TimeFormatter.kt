package com.example.modoomap.model

object TimeFormatter {
    fun formatTimeString(regTime: Long): String {
        val curTime = System.currentTimeMillis()
        val diffTime = (curTime - regTime) / 1000

        return when {
            diffTime < 60 -> "방금 전"
            diffTime < 60 * 60 -> "${diffTime / 60}분 전"
            diffTime < 60 * 60 * 24 -> "${diffTime / (60 * 60)}시간 전"
            diffTime < 60 * 60 * 24 * 30 -> "${diffTime / (60 * 60 * 24)}일 전"
            else -> "${diffTime / (60 * 60 * 24 * 30)}달 전"
        }
    }
}
