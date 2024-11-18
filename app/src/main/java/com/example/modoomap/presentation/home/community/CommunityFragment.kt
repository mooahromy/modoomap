package com.example.modoomap.presentation.home.community

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.modoomap.databinding.FragmentCommunityBinding
import com.example.modoomap.model.Posting
import com.example.modoomap.presentation.posting.detail.PostingDetailActivity
import com.example.modoomap.presentation.posting.making.PostingMakingActivity
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class CommunityFragment :
    Fragment(),
    PostingClickListener {
    private lateinit var binding: FragmentCommunityBinding
    private lateinit var postings: List<Posting>
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCommunityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()
        binding.fabWritePosting.setOnClickListener {
            startActivity(PostingMakingActivity.createIntent(requireContext()))
        }
    }

    override fun onStart() {
        super.onStart()
        getAllPosts()
    }

    private fun getAllPosts() {
        db
            .collection("postings")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    println("posting : ${document.id} => ${document.data}")
                }
                postings = documents.map { documentToPosting(it) }.sortedByDescending { it.createdAt }
                val adapter = PostingAdapter(postings, this)
                binding.rvPostings.adapter = adapter
            }.addOnFailureListener { exception ->
                println("Error getting documents: $exception")
            }
    }

    private fun documentToPosting(document: DocumentSnapshot): Posting =
        Posting(
            id = document.id,
            profileImg = imageUrl(document),
            username = username(document),
            title = document.getString("title") ?: "제목을 불러오는 중 오류가 발생했습니다.",
            content = document.getString("content") ?: "내용을 불러오는 중 오류가 발생했습니다.",
            commentCount = document.getLong("commentCount") ?: 0L,
            createdAt = document.getLong("createdAt") ?: 0L,
        )

    override fun onClick(posting: Posting) {
        startActivity(PostingDetailActivity.createIntent(posting.id, requireContext()))
    }

    private fun imageUrl(document: DocumentSnapshot): String {
        val url = document.getString("authorImage")
        return if (url.isNullOrEmpty()) {
            "https://img.freepik.com/premium-vector/default-avatar-profile-icon-social-media-user-image-gray-avatar-icon-blank-profile-silhouette-vector-illustration_561158-3383.jpg?semt=ais_hybrid"
        } else {
            url
        }
    }

    private fun username(document: DocumentSnapshot): String {
        val username = document.getString("author")
        return if (username.isNullOrEmpty()) {
            "이름을 불러오는 중 오류가 발생했습니다."
        } else {
            username
        }
    }

    companion object {
        fun newInstance(): CommunityFragment = CommunityFragment()
    }
}
