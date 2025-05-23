// java/com/example/safeaid/screens/community/CommentActivity.kt
package com.example.safeaid.screens.community

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidtraining.databinding.ActivityCommentBinding
import com.example.safeaid.core.utils.DataResult
import com.example.safeaid.core.utils.UserManager
import com.example.safeaid.screens.community.data.CommentItem
import com.example.safeaid.screens.community.viewmodel.CommentEvent
import com.example.safeaid.screens.community.viewmodel.CommentState
import com.example.safeaid.screens.community.viewmodel.CommentViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.text.clear

@AndroidEntryPoint
class CommentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommentBinding
    private val vm: CommentViewModel by viewModels()
    private lateinit var adapter: CommentAdapter
    private var lastTypedComment: String = ""
    private lateinit var postId: String

    @Inject
    lateinit var userManager: UserManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get post ID from intent
        postId = intent.getStringExtra("post_id") ?: ""


        // nút back
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }


        // RecyclerView
        adapter = CommentAdapter(
            emptyList(),
            userManager,
            onDeleteClick = { commentId ->
                deleteComment(commentId)
            }
        )
        binding.rvComments.apply {
            layoutManager = LinearLayoutManager(this@CommentActivity)
            adapter       = this@CommentActivity.adapter
        }

        // gửi bình luận
        binding.btnSendComment.setOnClickListener {
            val txt = binding.etComment.text.toString().trim()
            lastTypedComment = txt
            if (txt.isNotEmpty()) {
                vm.onTriggerEvent(CommentEvent.Post(txt))
            }
        }

        // observe ViewModel.stateFlow → LiveData
        vm.viewState
            .asLiveData()
            .observe(this) { result ->
                when (result) {
                    is DataResult.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.tvEmptyState.visibility = View.GONE
                    }
                    is DataResult.Success<*> -> {
                        binding.progressBar.visibility = View.GONE
                        when (val st = result.data as? CommentState) {
                            is CommentState.Success -> {
                                val list = st.items.map { dto ->
                                    val time = dto.timeAgo
                                    val content = dto.content ?: ""
                                    CommentItem(
                                        id = dto.commentId ?: "",
                                        userName = dto.user?.username ?: "Bạn",
                                        time = time ?: "Ngay bây giờ",
                                        content = content,
                                        profileImagePath = dto.user?.avatarPath
                                    )
                                }
                                adapter.updateData(list)

                                // Show empty state if list is empty
                                if (list.isEmpty()) {
                                    binding.tvEmptyState.visibility = View.VISIBLE
                                    binding.rvComments.visibility = View.GONE
                                } else {
                                    binding.tvEmptyState.visibility = View.GONE
                                    binding.rvComments.visibility = View.VISIBLE
                                    // scroll lên đầu sau khi load xong
                                    binding.rvComments.post {
                                        binding.rvComments.scrollToPosition(0)
                                    }
                                }
                            }
                            is CommentState.Added -> {
                                // thêm comment mới vào đầu
                                val dto  = st.comment
                                val displayContent = dto.content.takeUnless { it.isNullOrBlank() }
                                    ?: lastTypedComment
                                val time = dto.createdAt?.substring(11,16) ?: ""
                                val new  = CommentItem(
                                    id       = dto.postId ?: "",
                                    userName = dto.user?.username ?: "Bạn",
                                    time     = time,
                                    content  = displayContent
                                )
                                val updated = adapter.getData().toMutableList().apply {
                                    add(0, new)
                                }
                                adapter.updateData(updated)
                                binding.etComment.text?.clear()

                                // Hide empty state when comment is added
                                binding.tvEmptyState.visibility = View.GONE
                                binding.rvComments.visibility = View.VISIBLE
                                binding.rvComments.post { binding.rvComments.scrollToPosition(0) }
                            }
                            is CommentState.Deleted -> {
                                adapter.removeItem(st.commentId)
                                if (adapter.itemCount == 0) {
                                    binding.tvEmptyState.visibility = View.VISIBLE
                                    binding.rvComments.visibility = View.GONE
                                }
                                Toast.makeText(this, "Đã xóa bình luận", Toast.LENGTH_SHORT).show()
                            }
                            is CommentState.Error -> {
                                // Only show error Toast for actual errors, not empty lists
                                if (st.message != "No comments found") {
                                    Toast.makeText(this, st.message, Toast.LENGTH_LONG).show()
                                } else {
                                    // Handle empty comments list
                                    binding.tvEmptyState.visibility = View.VISIBLE
                                    binding.rvComments.visibility = View.GONE
                                }
                            }
                            else -> { /* no-op */ }
                        }
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
                        // Có thể là `null` hoặc dòng emission đầu tiên
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }

        // load danh sách lần đầu
        vm.onTriggerEvent(CommentEvent.Load)
    }

    private fun deleteComment(commentId: String) {
        vm.onTriggerEvent(CommentEvent.Delete(commentId))
    }
}
