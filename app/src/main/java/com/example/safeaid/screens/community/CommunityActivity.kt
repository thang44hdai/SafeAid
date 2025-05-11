// CommunityActivity.kt
package com.example.safeaid.screens.community

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidtraining.databinding.ActivityCommunityBinding
import com.example.safeaid.core.utils.DataResult
import com.example.safeaid.screens.community.viewmodel.CommunityState
import com.example.safeaid.screens.community.viewmodel.CommunityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommunityActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCommunityBinding
    private val viewModel: CommunityViewModel by viewModels()
    private lateinit var adapter: CommunityAdapter

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

        // RecyclerView + adapter
        adapter = CommunityAdapter(emptyList()) { post ->
            startActivity(
                Intent(this, CommentActivity::class.java)
                    .putExtra("post_id", post.post_id)
            )
        }
        binding.rvPosts.apply {
            layoutManager = LinearLayoutManager(this@CommunityActivity)
            adapter = this@CommunityActivity.adapter
        }

        // observe the StateFlow<DataResult<CommunityState>> by converting it to LiveData
        viewModel.viewState
            .asLiveData()
            .observe(this, Observer { result ->
                // result is DataResult<CommunityState>?
                when (result) {
                    is DataResult.Success<*> -> {
                        // hide any network‐error
                        binding.progressBar.visibility = View.GONE

                        // unwrap the inner CommunityState
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
                            else -> { /* no‐op */ }
                        }
                    }
                    is DataResult.Loading -> {
                        // network in‐flight
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is DataResult.Error -> {
                        // network‐level failure
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(
                            this,
                            "Network error: ${result.error.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    else -> {
                        // first emission might be null
                    }
                }
            })

        // trigger initial load
        viewModel.onTriggerEvent(com.example.safeaid.screens.community.viewmodel.CommunityEvent.LoadPosts)
    }
}
