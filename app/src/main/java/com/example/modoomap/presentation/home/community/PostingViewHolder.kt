package com.example.modoomap.presentation.home.community

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.modoomap.databinding.ItemPostingBinding
import com.example.modoomap.model.Posting
import com.example.modoomap.model.TimeFormatter

class PostingViewHolder(
    private val binding: ItemPostingBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(posting: Posting) {
        binding.tvUsername.text = posting.username
        binding.tvTitle.text = posting.title
        binding.tvContent.text = posting.content
        binding.tvCommetCount.text = posting.commentCount.toString()
        binding.tvPostingTime.text = TimeFormatter.formatTimeString(posting.createdAt)

        Glide
            .with(binding.root)
            .load(posting.profileImg)
            .into(binding.ivProfile)
    }
}
