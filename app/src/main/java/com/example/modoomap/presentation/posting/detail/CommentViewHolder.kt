package com.example.login.presentation.posting.detail

import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.modoomap.R
import com.example.modoomap.databinding.ItemCommentBinding
import com.example.modoomap.model.Comment
import com.example.modoomap.model.TimeFormatter
import com.example.modoomap.presentation.posting.detail.CommentMenuClickListener

class CommentViewHolder(
    private val binding: ItemCommentBinding,
    private val currentUserEmail: String,
    private val commentMenuClickListener: CommentMenuClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(comment: Comment) {
        binding.tvUsername.text = comment.username
        binding.tvContent.text = comment.content
        binding.tvCreatedAt.text = TimeFormatter.formatTimeString(comment.createdAt)

        Glide
            .with(binding.root)
            .load(comment.userProfile)
            .into(binding.ivProfile)

        binding.ivMenu.setOnClickListener {
            val popupMenu = PopupMenu(binding.root.context, binding.ivMenu)
            popupMenu.menuInflater.inflate(R.menu.comment_menu, popupMenu.menu)
            if (comment.username == currentUserEmail) {
                popupMenu.menu.findItem(R.id.action_delete).isVisible = true
                popupMenu.menu.findItem(R.id.action_report).isVisible = false
            } else {
                popupMenu.menu.findItem(R.id.action_delete).isVisible = false
                popupMenu.menu.findItem(R.id.action_report).isVisible = true
            }
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_delete -> {
                        // 댓글 삭제 처리
                        commentMenuClickListener.onCommentDelete(comment)
                        true
                    }
                    R.id.action_report -> {
                        // 댓글 신고 처리
                        commentMenuClickListener.onCommentReport(comment)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }
}
