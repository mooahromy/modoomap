package com.example.modoomap.presentation.home.mypage

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.modoomap.databinding.FragmentMyPageBinding
import com.example.modoomap.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyPageFragment : Fragment() {
    private lateinit var binding: FragmentMyPageBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var currentUser: User
    private lateinit var currentUserEmail: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMyPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        currentUserEmail = auth.currentUser?.email ?: ""
        getUserInfo()

        binding.tvAlert.setOnClickListener {
            val phoneNumber = "tel:112"
            val dialIntent =
                Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse(phoneNumber)
                }
            startActivity(dialIntent)
        }
    }

    private fun getUserInfo() {
        db
            .collection("users")
            .document(currentUserEmail)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                currentUser =
                    User(
                        username = documentSnapshot.getString("username") ?: "닉네임이 아직 설정되지 않았어요",
                        profileImg = documentSnapshot.getString("profileImage") ?: DEFAULT_PROFILE,
                    )
                binding.tvUsername.text = currentUser.username
                binding.tvEmail.text = currentUserEmail
                Glide.with(binding.root).load(currentUser.profileImg).into(binding.ivProfile)
            }
    }

    companion object {
        private const val DEFAULT_PROFILE =
            "https://img.freepik.com/premium-vector/default-avatar-profile-icon-social-media-user-image-gray-avatar-icon-blank-profile-silhouette-vector-illustration_561158-3383.jpg?semt=ais_hybrid"
    }
}
