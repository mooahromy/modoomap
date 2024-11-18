package com.example.modoomap.presentation.home.community

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.modoomap.databinding.ItemPostingBinding
import com.example.modoomap.model.Posting

class PostingAdapter(
    private val postings: List<Posting>,
    private val onItemClickListener: PostingClickListener,
) : RecyclerView.Adapter<PostingViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PostingViewHolder {
        val binding = ItemPostingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostingViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: PostingViewHolder,
        position: Int,
    ) {
        holder.bind(postings[position])
        holder.itemView.setOnClickListener {
            onItemClickListener.onClick(postings[position])
        }
    }

    override fun getItemCount(): Int = postings.size
}
