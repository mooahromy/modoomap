package com.example.modoomap.presentation.posting.making

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.modoomap.databinding.ActivityPostingMakingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PostingMakingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostingMakingBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostingMakingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.ivCancel.setOnClickListener {
            finish()
        }

        binding.ivComplete.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val content = binding.etContent.text.toString()
            var username = ""
            var userProfileImage = ""

            binding.clLoading.visibility = View.VISIBLE
            db
                .collection("users")
                .document(auth.currentUser?.email.toString())
                .get()
                .addOnSuccessListener {
                    username = it.getString("username") ?: auth.currentUser?.email.toString()
                    userProfileImage = it.getString("profileImage") ?: ""
                    savePostingData(title, content, username, userProfileImage)
                }
        }
    }

    private fun savePostingData(
        title: String,
        content: String,
        username: String,
        userProfileImage: String,
    ) {
        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "제목과 내용을 입력해주세요", Toast.LENGTH_SHORT).show()
            binding.clLoading.visibility = View.GONE
            return
        }

        if (title.isNotEmpty() && content.isNotEmpty()) {
            val posting =
                hashMapOf(
                    "author" to username,
                    "authorImage" to userProfileImage,
                    "title" to title,
                    "content" to content,
                    "createdAt" to System.currentTimeMillis(),
                    "commentCount" to 0L,
                )
            db
                .collection("postings")
                .add(posting)
                .addOnSuccessListener {
                    Toast.makeText(this, "게시글이 작성되었습니다", Toast.LENGTH_SHORT).show()
                    binding.clLoading.visibility = View.GONE
                    finish()
                }.addOnFailureListener {
                    // Handle error
                    Toast.makeText(this, "게시글 작성에 실패했습니다", Toast.LENGTH_SHORT).show()
                    binding.clLoading.visibility = View.GONE
                }
        }
    }

    companion object {
        fun createIntent(context: Context): Intent = Intent(context, PostingMakingActivity::class.java)
    }
}
