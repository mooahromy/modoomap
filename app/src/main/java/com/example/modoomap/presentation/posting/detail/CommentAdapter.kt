package com.example.modoomap.presentation.posting.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.login.presentation.posting.detail.CommentViewHolder
import com.example.modoomap.databinding.ItemCommentBinding
import com.example.modoomap.model.Comment

class CommentAdapter(
    private val comments: List<Comment>,
    private val currentUserEmail: String,
    private val commentMenuClickListener: CommentMenuClickListener,
) : RecyclerView.Adapter<CommentViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CommentViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding, currentUserEmail, commentMenuClickListener)
    }

    override fun onBindViewHolder(
        holder: CommentViewHolder,
        position: Int,
    ) {
        holder.bind(comments[position])
    }

    override fun getItemCount(): Int = comments.size
}
