package com.example.modoomap.presentation.sign

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.modoomap.R
import com.example.modoomap.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        binding.ivCheck.isSelected = false

        binding.ivCheck.setOnClickListener {
            binding.ivCheck.isSelected = !binding.ivCheck.isSelected
            binding.ivCheck.setImageResource(
                if (binding.ivCheck.isSelected) {
                    R.drawable.ic_checked
                } else {
                    R.drawable.ic_unchecked
                },
            )
        }

        binding.tvCheck.setOnClickListener {
            binding.ivCheck.isSelected = !binding.ivCheck.isSelected
            binding.ivCheck.setImageResource(
                if (binding.ivCheck.isSelected) {
                    R.drawable.ic_checked
                } else {
                    R.drawable.ic_unchecked
                },
            )
        }

        binding.tvNavPrev.setOnClickListener {
            finish()
        }

        binding.tvNavNext.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val passwordCheck = binding.etPasswordConfirm.text.toString()
            val birth = binding.etBirth.text.toString()
            val contact =
                binding.etContact.text
                    .toString()
                    .replace("-", "")

            if (email.isEmpty() ||
                password.isEmpty() ||
                passwordCheck.isEmpty() ||
                birth.isEmpty() ||
                contact.isEmpty()
            ) {
                Toast.makeText(this, "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != passwordCheck) {
                Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!binding.ivCheck.isSelected) {
                Toast.makeText(this, "약관에 동의해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (contact.length != 11) {
                Toast.makeText(this, "올바른 전화번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.clLoading.visibility = View.VISIBLE
            binding.clLoading.bringToFront()

            auth
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    binding.clLoading.visibility = View.GONE

                    if (task.isSuccessful) {
                        // 회원가입 성공
                        val user =
                            hashMapOf(
                                "email" to email,
                                "password" to password,
                                "birth" to birth,
                                "contact" to contact,
                                "createdAt" to System.currentTimeMillis(),
                            )
                        db
                            .collection("users")
                            .document(email)
                            .set(user)
                        Toast.makeText(this, "회원가입에 성공하였습니다.", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        // 회원가입 실패
                        Toast.makeText(this, "회원가입에 실패하였습니다.\n다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    companion object {
        fun createIntent(context: Context): Intent = Intent(context, SignupActivity::class.java)
    }
}
