// java/com/example/safeaid/screens/community/CommentActivity.kt
package com.example.safeaid.screens.community

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidtraining.databinding.ActivityCommentBinding
import com.example.safeaid.core.utils.DataResult
import com.example.safeaid.screens.community.data.CommentItem
import com.example.safeaid.screens.community.viewmodel.CommentEvent
import com.example.safeaid.screens.community.viewmodel.CommentState
import com.example.safeaid.screens.community.viewmodel.CommentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommentBinding
    private val vm: CommentViewModel by viewModels()
    private lateinit var adapter: CommentAdapter
    private var lastTypedComment: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // nút back
        binding.ivBack.setOnClickListener { finish() }

        // RecyclerView
        adapter = CommentAdapter(emptyList())
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
                    }
                    is DataResult.Success<*> -> {
                        binding.progressBar.visibility = View.GONE
                        when (val st = result.data as? CommentState) {
                            is CommentState.Success -> {
                                val list = st.items.map { dto ->
                                    val time = dto.createdAt?.substring(11,16) ?: ""
                                    val content = dto.content ?: ""
                                    CommentItem(
                                        userName = dto.user?.username ?: "Bạn",
                                        time     = time,
                                        content  = content
                                    )
                                }
                                adapter.updateData(list)
                                // scroll lên đầu sau khi load xong
                                binding.rvComments.post {
                                    if (list.isNotEmpty()) binding.rvComments.scrollToPosition(0)
                                }
                            }
                            is CommentState.Added -> {
                                // thêm comment mới vào đầu
                                val dto  = st.comment
                                val displayContent = dto.content.takeUnless { it.isNullOrBlank() }
                                    ?: lastTypedComment
                                val time = dto.createdAt?.substring(11,16) ?: ""
                                val new  = CommentItem(
                                    userName = dto.user?.username ?: "Bạn",
                                    time     = time,
                                    content  = displayContent
                                )
                                val updated = adapter.getData().toMutableList().apply {
                                    add(0, new)
                                }
                                adapter.updateData(updated)
                                binding.etComment.text?.clear()
                                binding.rvComments.post { binding.rvComments.scrollToPosition(0) }
                            }
                            is CommentState.Error -> {
                                Toast.makeText(this, st.message, Toast.LENGTH_LONG).show()
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
}
