package com.example.safeaid.screens.home

import android.content.Intent
import android.text.format.DateUtils
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.safeaid.core.ui.BaseFragment
import com.example.androidtraining.databinding.FragmentHomeBinding
import com.example.safeaid.MainNavigator
import com.example.safeaid.core.response.NewsDto
import com.example.safeaid.core.ui.BaseContainerFragment
import com.example.safeaid.core.utils.DataResult
import com.example.safeaid.core.utils.UserManager
import com.example.safeaid.core.utils.setOnDebounceClick
import com.example.safeaid.screens.community.CommentActivity
import com.example.safeaid.screens.community.CommunityActivity
import com.example.safeaid.screens.community.CommunityAdapter
import com.example.safeaid.screens.community.data.PostDto
import com.example.safeaid.screens.community.viewmodel.CommunityEvent
import com.example.safeaid.screens.community.viewmodel.CommunityState
import com.example.safeaid.screens.community.viewmodel.CommunityViewModel
import com.example.safeaid.screens.leaderboard.LeaderboardActivity
import com.example.safeaid.screens.news.NewsActivity
import com.example.safeaid.screens.news.viewmodel.NewsState
import com.example.safeaid.screens.news.viewmodel.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    private val communityVM: CommunityViewModel by viewModels(
        ownerProducer = { requireActivity() }
    )

    private val newsVM: NewsViewModel by viewModels(
        ownerProducer = { requireActivity() }
    )

    @Inject
    lateinit var userManager: UserManager

    private lateinit var communityAdapter: CommunityAdapter

    private lateinit var newsAdapter: NewsAdapter

    private val mainNavigator: MainNavigator by activityViewModels()

    override fun isHostFragment(): Boolean = true

    override fun onInit() {
        loadUserProfile()

        // 1) Thiết lập RecyclerView cho Community
        communityAdapter = CommunityAdapter(
            items = emptyList(),
            onCommentsClick = { post ->
                startActivity(
                    Intent(requireContext(), CommentActivity::class.java)
                        .putExtra("post_id", post.post_id)
                )
            },
            onLikeToggle = { post, nowLiked ->
                if (nowLiked)
                    communityVM.onTriggerEvent(CommunityEvent.LikePost(post.post_id))
                else
                    communityVM.onTriggerEvent(CommunityEvent.UnLikePost(post.post_id))
            },
            onShareClick = { post ->
                sharePost(post)
            }
        )

        viewBinding.searchContainer.setOnClickListener {
            startActivity(Intent(requireContext(), SearchActivity::class.java))
        }

        viewBinding.rvHomeCommunity.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = communityAdapter
        }

        // 2) Trigger load 5 post đầu tiên (tùy API hỗ trợ)
        communityVM.loadPosts(page = 1)

        // RecyclerView Tin tức

        newsAdapter = NewsAdapter(emptyList()) { dto ->
            startActivity(
                Intent(requireContext(), NewsActivity::class.java)
                    .putExtra("news_id", dto.id)
            )
        }
        viewBinding.rvNews.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = newsAdapter
            isNestedScrollingEnabled = false
        }
        newsVM.loadNews()
    }

    private fun loadUserProfile() {
        userManager.ensureProfileLoaded(requireContext()) { success ->
            if (success) {
                updateUserInterface()
            } else {
                Log.e("HomeFragment", "Failed to load user profile")
            }
        }
    }

    private fun updateUserInterface() {
        // Get username and display it
        val username = userManager.getUsername(requireContext())
        viewBinding.textView?.text = "Xin chào,\n@${username}".ifEmpty { "Người dùng" }

        // Load avatar image
        val avatarUrl = userManager.getAvatarUrl(requireContext())
        if (avatarUrl.isNotEmpty()) {
            viewBinding.imageView2?.let { imageView ->
                Glide.with(requireContext())
                    .load(avatarUrl)
                    .placeholder(com.example.androidtraining.R.drawable.default_avt)
                    .error(com.example.androidtraining.R.drawable.default_avt)
                    .into(imageView)
            }
        }
    }


    override fun onInitObserver() {
        // Khi có dữ liệu trả về từ CommunityViewModel
        communityVM.viewState
            .asLiveData()
            .observe(viewLifecycleOwner) { result ->
                when (result) {
                    is DataResult.Success<*> -> {
                        when (val st = result.data as? CommunityState) {
                            is CommunityState.Success -> {
                                // chỉ lấy 5 post đầu tiên
                                val top5 = st.posts.take(5)
                                communityAdapter.updateList(top5)
                            }
                            else -> { /* Loading / Error xử lý nếu muốn */ }
                        }
                    }
                    is DataResult.Loading -> {
                        // có thể hiện progress nếu muốn
                    }
                    is DataResult.Error -> {
                        // show error
                        Toast.makeText(requireContext(),
                            "Lỗi tải bài viết: ${result.error.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    else -> {}
                }

            newsVM.viewState.observe(viewLifecycleOwner) { st ->
                when (st) {
                    is NewsState.Success -> {
                        // chỉ lấy 5 phần tử
                        newsAdapter.updateList(st.items.take(5))

                        // Populate the news items in the UI
                        val topNews = st.items.take(2)
                        Log.d("HomeFragment", "Top news: $topNews")
                        topNews.forEachIndexed { index, newsDto ->
                            bindNewsItem(index + 1, newsDto)
                        }
                    }
                    is NewsState.Error -> {
                        Toast.makeText(requireContext(),
                            "Lỗi tải tin tức: ${st.message}",
                            Toast.LENGTH_LONG).show()
                    }

                    NewsState.Loading -> {

                    }
                }
            }
        }
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

        viewBinding.menuItem2.setOnClickListener {
            startActivity(Intent(requireContext(), CommunityActivity::class.java))
        }

        viewBinding.tvXemThem.setOnClickListener {
            startActivity(Intent(requireContext(), NewsActivity::class.java))
        }

        newsVM.viewState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is NewsState.Success -> {
                    newsAdapter.updateList(state.items.take(5))
                }
                is NewsState.Error -> {
                    Toast.makeText(
                        requireContext(),
                        "Lỗi tải tin tức: ${state.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                // Loading nếu cần show Progress
                else -> {}
            }
        }

        viewBinding.imageButton3.setOnDebounceClick {
            mainNavigator.offerNavEvent(GoToNotificationScreen())
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh user data when returning to this fragment
        loadUserProfile()
    }

    private fun bindNewsItem(idx: Int, news: NewsDto) {
        if (idx < 1 || idx > 10) return

        // Create view accessor function using reflection
        fun view(prefix: String): Any? {
            val idName = "${prefix}${idx}"
            val fieldName = idName.replaceFirst("img", "imgNews").replaceFirst("tv", "tvNews")
            return try {
                val field = viewBinding::class.java.getDeclaredField(fieldName)
                field.isAccessible = true
                field.get(viewBinding)
            } catch (e: Exception) {
                Log.e("HomeFragment", "View not found: $fieldName", e)
                null
            }
        }

        // Get the views
        val iv = view("imgNews") as? ImageView ?: return
        val tvTitle = view("tvNewsTitle") as? TextView ?: return
        val tvTime = view("tvNewsTime") as? TextView ?: return

        // Load data
        Glide.with(iv)
            .load(news.thumbnail)
            .into(iv)

        tvTitle.text = news.title
        tvTime.text = news.timeAgo

        // Make clickable
        iv.setOnClickListener {
            val intent = Intent(requireContext(), NewsActivity::class.java)
                .putExtra("news_id", news.id)
            startActivity(intent)
        }
    }

    private fun sharePost(post: PostDto) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Check out this post")

            // Create share content with post details
            val shareText = "${post.user.username}: ${post.content}\n\nShared from SafeAid App"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }
}

class GoToQuizHistory() : BaseContainerFragment.NavigationEvent()
class GoToNotificationScreen() : BaseContainerFragment.NavigationEvent()
