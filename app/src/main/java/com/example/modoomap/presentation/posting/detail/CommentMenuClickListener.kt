package com.example.modoomap.presentation.posting.detail

import com.example.modoomap.model.Comment

interface CommentMenuClickListener {
    fun onCommentDelete(comment: Comment)

    fun onCommentReport(comment: Comment)
}
