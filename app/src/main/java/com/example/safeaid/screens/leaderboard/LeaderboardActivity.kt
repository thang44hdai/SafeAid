package com.example.safeaid.screens.leaderboard

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.androidtraining.R
import com.example.androidtraining.databinding.ActivityLeaderboardBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LeaderboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLeaderboardBinding

    private val tabTitles = listOf("Bảng xếp hạng", "Cá nhân")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLeaderboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // set Toolbar back
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        // Adapter cho ViewPager2
        binding.viewPager.adapter = LeaderboardPagerAdapter(this)

        // Liên kết TabLayout và ViewPager2
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }
}
