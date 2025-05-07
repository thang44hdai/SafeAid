package com.example.safeaid.screens.home

import android.content.Intent
import androidx.fragment.app.activityViewModels
import com.example.safeaid.core.ui.BaseFragment
import com.example.androidtraining.databinding.FragmentHomeBinding
import com.example.safeaid.MainNavigator
import com.example.safeaid.core.ui.BaseContainerFragment
import com.example.safeaid.core.utils.setOnDebounceClick
import com.example.safeaid.screens.community.CommunityActivity
import com.example.safeaid.screens.leaderboard.LeaderboardActivity
import com.example.safeaid.screens.news.NewsActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    private val mainNavigator: MainNavigator by activityViewModels()

    override fun isHostFragment(): Boolean = true

    override fun onInit() {
        // Khởi tạo các thành phần khác nếu cần
    }

    override fun onInitObserver() {
        // Các observer nếu có
    }

    override fun onInitListener() {
        // Sử dụng viewBinding (do BaseFragment của bạn khai báo biến viewBinding)
        viewBinding.menuItem2.setOnClickListener {
            val intent = Intent(requireContext(), CommunityActivity::class.java)
            startActivity(intent)
        }
        viewBinding.menuItem1.setOnClickListener {
            val intent = Intent(requireContext(), NewsActivity::class.java)
            startActivity(intent)
        }

        viewBinding.leaderboardBtn.setOnClickListener {
            val intent = Intent(requireContext(), LeaderboardActivity::class.java)
            startActivity(intent)
        }
        viewBinding.menuItem4.setOnDebounceClick {
            mainNavigator.offerNavEvent(GoToQuizHistory())
        }
    }
}

class GoToQuizHistory() : BaseContainerFragment.NavigationEvent()
