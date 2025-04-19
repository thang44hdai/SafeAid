package com.example.safeaid.screens.leaderboard

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class LeaderboardPagerAdapter(activity: LeaderboardActivity)
    : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> RankingFragment()
            else -> PersonalFragment()
        }
    }
}
