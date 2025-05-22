package com.example.safeaid.screens.guide.tablayout

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.safeaid.core.response.GuideStepResponse
import com.example.safeaid.screens.guide.tablayout.GuideTabFragment

class GuideDetailPagerAdapter(
    fragment: Fragment,
    private val steps: List<GuideStepResponse>,
    private val categoryId: String,
    private val guideId: String // Thêm guideId parameter
) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3 // Tăng số lượng tab lên 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> GuideTabFragment.newInstance(ArrayList(steps))
            1 -> RelatedTabFragment.newInstance(categoryId)
            2 -> ReviewTabFragment.newInstance(guideId)
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }
}