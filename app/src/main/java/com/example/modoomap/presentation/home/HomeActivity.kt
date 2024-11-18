package com.example.modoomap.presentation.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.modoomap.R
import com.example.modoomap.databinding.ActivityHomeBinding
import com.example.modoomap.presentation.home.community.CommunityFragment
import com.example.modoomap.presentation.home.map.MapFragment
import com.example.modoomap.presentation.home.mypage.MyPageFragment

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add(binding.fragmentContainer.id, CommunityFragment.newInstance())
            }
        }
        setBottomNavigation()

        // 두번째 메뉴가 선택되어 있도록 설정
        binding.bottomNavigationView.selectedItemId = R.id.menu_posting
    }

    private fun setBottomNavigation() {
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_posting -> {
                    supportFragmentManager.commit {
                        replace(binding.fragmentContainer.id, CommunityFragment())
                    }
                    true
                }

                R.id.menu_map -> {
                    supportFragmentManager.commit {
                        replace(binding.fragmentContainer.id, MapFragment())
                    }
                    true
                }

                R.id.menu_profile -> {
                    supportFragmentManager.commit {
                        replace(binding.fragmentContainer.id, MyPageFragment())
                    }
                    true
                }

                else -> {
                    false
                }
            }
        }
    }

    companion object {
        fun createIntent(context: Context): Intent = Intent(context, HomeActivity::class.java)
    }
}
