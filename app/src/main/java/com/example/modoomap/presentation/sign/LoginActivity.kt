package com.example.modoomap.presentation.sign

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.modoomap.databinding.ActivityLoginBinding
import com.example.modoomap.presentation.home.HomeActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private var isSplashScreenVisible = true

    override fun onCreate(savedInstanceState: Bundle?) {
        setupSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        setContentView(binding.root)

        binding.tvLoginBtn.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            auth
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // 로그인 성공
                        startActivity(HomeActivity.createIntent(this))
                        finish()
                    } else {
                        // 로그인 실패
                        Toast.makeText(this, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        binding.tvSignup.setOnClickListener {
            // 회원가입 화면으로 이동
            startActivity(SignupActivity.createIntent(this))
        }
    }

    private fun setupSplashScreen() {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { isSplashScreenVisible }

        // 스플래시 화면 2초 설정
        Handler(Looper.getMainLooper()).postDelayed({
            isSplashScreenVisible = false
        }, 2000)
    }
}
