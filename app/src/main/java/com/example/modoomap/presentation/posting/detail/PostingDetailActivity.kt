package com.example.modoomap.presentation.posting.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.modoomap.databinding.ActivityPostingDetailBinding
import com.example.modoomap.model.Comment
import com.example.modoomap.model.Posting
import com.example.modoomap.model.TimeFormatter
import com.example.modoomap.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class PostingDetailActivity :
    AppCompatActivity(),
    CommentMenuClickListener {
    private lateinit var binding: ActivityPostingDetailBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var comments: List<Comment> = emptyList()
    private lateinit var currentUser: User
    private lateinit var postingId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostingDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        postingId = intent.getStringExtra(INTENT_ID) ?: ""
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        initViews()
        initCurrentUser()

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.ivSend.setOnClickListener {
            val content = binding.etComment.text.toString()
            if (content.isEmpty()) {
                Toast.makeText(this, "내용을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val comment =
                hashMapOf(
                    "content" to content,
                    "username" to currentUser.username,
                    "userProfile" to currentUser.profileImg,
                    "email" to (auth.currentUser?.email ?: ""),
                    "createdAt" to System.currentTimeMillis(),
                )
            db
                .collection("postings")
                .document(postingId)
                .collection("comments")
                .add(comment)
                .addOnSuccessListener {
                    binding.etComment.text.clear()
                    fetchComments()
                }
        }
    }

    private fun initViews() {
        println("postingId : $postingId")
        db
            .collection("postings")
            .document(postingId)
            .get()
            .addOnSuccessListener { document ->
                val posting = documentToPosting(document)
                println(posting)
                initPostingDetailView(posting)
            }
        fetchComments()
    }

    private fun initCurrentUser() {
        val email = auth.currentUser?.email ?: ""
        db
            .collection("users")
            .document(email)
            .get()
            .addOnSuccessListener { document ->
                currentUser =
                    User(
                        username = document.getString("username") ?: email,
                        profileImg =
                            document.getString("profileImage")
                                ?: DEFAULT_PROFILE,
                    )
            }
    }

    private fun documentToPosting(document: DocumentSnapshot): Posting =
        Posting(
            id = document.id,
            profileImg = profileImageUrl(document),
            username = document.getString("author") ?: "",
            title = document.getString("title") ?: "",
            content = document.getString("content") ?: "",
            commentCount = document.getLong("commentCount") ?: 0L,
            createdAt = document.getLong("createdAt") ?: 0L,
        )

    private fun profileImageUrl(document: DocumentSnapshot): String {
        val image = document.getString("authorImage")
        if (image.isNullOrEmpty()) return DEFAULT_PROFILE
        return image
    }

    private fun initPostingDetailView(posting: Posting) {
        Glide
            .with(this)
            .load(posting.profileImg)
            .into(binding.ivProfile)
        binding.tvUsername.text = posting.username
        binding.tvTitle.text = posting.title
        binding.tvContent.text = posting.content
        binding.tvCommetCount.text = posting.commentCount.toString()
        binding.tvPostingTime.text = TimeFormatter.formatTimeString(posting.createdAt)
        println("real posting: $posting")
    }

    private fun fetchComments() {
        db
            .collection("postings")
            .document(postingId)
            .collection("comments")
            .get()
            .addOnSuccessListener { documents ->
                comments = documents.map { documentToComment(it) }.sortedBy { it.createdAt }
                val adapter = CommentAdapter(comments, auth.currentUser?.email ?: "", this)
                binding.rvComments.adapter = adapter
            }
    }

    private fun documentToComment(document: DocumentSnapshot): Comment =
        Comment(
            id = document.id,
            postingId = document.getString("postingId") ?: "",
            content = document.getString("content") ?: "",
            username = document.getString("username") ?: "",
            email = document.getString("email") ?: "",
            userProfile = document.getString("userProfile") ?: DEFAULT_PROFILE,
            createdAt = document.getLong("createdAt") ?: 0L,
        )

    override fun onCommentDelete(comment: Comment) {
        db
            .collection("postings")
            .document(postingId)
            .collection("comments")
            .document(comment.id)
            .delete()
            .addOnSuccessListener {
                fetchComments()
            }
    }

    override fun onCommentReport(comment: Comment) {
        Toast.makeText(this, "신고 되었습니다.", Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val INTENT_ID = "id"
        private const val DEFAULT_PROFILE = "https://img.freepik.com/premium-vector/default-avatar-profile-icon-social-media-user-image-gray-avatar-icon-blank-profile-silhouette-vector-illustration_561158-3383.jpg?semt=ais_hybrid"

        fun createIntent(
            id: String,
            context: Context,
        ): Intent =
            Intent(context, PostingDetailActivity::class.java).apply {
                putExtra(INTENT_ID, id)
            }
    }
}
