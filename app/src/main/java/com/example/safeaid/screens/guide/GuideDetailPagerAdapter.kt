package com.example.safeaid.screens.guide

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.safeaid.core.response.GuideStepResponse

class GuideDetailPagerAdapter(
    fragment: Fragment,
    private val steps: List<GuideStepResponse>
) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> GuideTabFragment.newInstance(ArrayList(steps))
//            1 -> RelatedTabFragment()
//            2 -> ReviewTabFragment()
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }
}