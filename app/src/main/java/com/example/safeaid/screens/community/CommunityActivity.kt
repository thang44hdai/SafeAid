// CommunityActivity.kt
package com.example.safeaid.screens.community

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidtraining.databinding.ActivityCommunityBinding
import com.example.safeaid.core.utils.DataResult
import com.example.safeaid.screens.community.data.PostDto
import com.example.safeaid.screens.community.viewmodel.CommunityEvent
import com.example.safeaid.screens.community.viewmodel.CommunityState
import com.example.safeaid.screens.community.viewmodel.CommunityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.jvm.java

@AndroidEntryPoint
class CommunityActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCommunityBinding
    private val viewModel: CommunityViewModel by viewModels()
    private lateinit var adapter: CommunityAdapter

    private val createPostLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val newId = result.data?.getStringExtra("new_post_id")
            viewModel.loadPosts(1)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityCommunityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val sb = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sb.left, sb.top, sb.right, sb.bottom)
            insets
        }

        binding.cardShare.setOnClickListener {
            createPostLauncher.launch(Intent(this, CreatePostActivity::class.java))
        }

        val swipe = binding.swipeRefresh
        swipe.setOnRefreshListener {
            viewModel.loadPosts(1)
            swipe.isRefreshing = false
        }

        adapter = CommunityAdapter(
            items = emptyList(),
            onCommentsClick = { post ->
                startActivity(
                    Intent(this, CommentActivity::class.java)
                        .putExtra("post_id", post.post_id)
                )
            },
            onLikeToggle = { post, nowLiked ->
                if (nowLiked) viewModel.onTriggerEvent(CommunityEvent.LikePost(post.post_id))
                else         viewModel.onTriggerEvent(CommunityEvent.UnLikePost(post.post_id))
            },
            onShareClick = { post ->
                sharePost(post)
            }
        )
        binding.rvPosts.apply {
            layoutManager = LinearLayoutManager(this@CommunityActivity)
            adapter = this@CommunityActivity.adapter
        }

        viewModel.viewState
            .asLiveData()
            .observe(this, Observer { result ->
                when (result) {
                    is DataResult.Success<*> -> {
                        binding.progressBar.visibility = View.GONE

                        when (val state = result.data as? CommunityState) {
                            is CommunityState.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                            }
                            is CommunityState.Success -> {
                                adapter.updateList(state.posts)

                            }
                            is CommunityState.Error -> {
                                Toast.makeText(
                                    this,
                                    "Lỗi: ${state.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            else -> {}
                        }
                    }
                    is DataResult.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is DataResult.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(
                            this,
                            "Network error: ${result.error.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    else -> {
                    }
                }
            })

        viewModel.onTriggerEvent(com.example.safeaid.screens.community.viewmodel.CommunityEvent.LoadPosts)
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
