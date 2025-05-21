package com.example.safeaid.screens.guide.tablayout

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.safeaid.core.response.GuideStepResponse
import com.example.safeaid.screens.guide.tablayout.GuideTabFragment

class GuideDetailPagerAdapter(
    fragment: Fragment,
    private val steps: List<GuideStepResponse>,
    private val categoryId: String // Thêm categoryId parameter
) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> GuideTabFragment.Companion.newInstance(ArrayList(steps))
            1 -> RelatedTabFragment.Companion.newInstance(categoryId)
            2 -> throw IllegalArgumentException("Review tab not implemented yet")
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }
}