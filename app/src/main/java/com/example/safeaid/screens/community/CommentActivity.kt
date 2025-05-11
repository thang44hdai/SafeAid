package com.example.safeaid.screens.community

import com.example.safeaid.screens.community.data.CommentItem
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidtraining.databinding.ActivityCommentBinding

class CommentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCommentBinding
    private lateinit var commentAdapter: CommentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    private fun setupUI() {
        // Xử lý nút back
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        // Tạo một danh sách comment mẫu (nếu bạn chưa có dữ liệu từ server)
        val sampleComments = listOf(
            CommentItem("Quoc Viet", "06:36", "Contrary to popular belief, Lorem Ipsum is not simply random text..."),
            CommentItem("Quang Thang", "07:05", "Contrary to popular belief, Lorem Ipsum is not simply random text..."),
            CommentItem("Nguyen Dinh Hau", "07:10", "Contrary to popular belief, Lorem Ipsum is not simply random text..."),
            CommentItem("Quoc Viet", "08:00", "Contrary to popular belief, Lorem Ipsum is not simply random text...")
        )

        // Khởi tạo Adapter và gán cho RecyclerView
        commentAdapter = CommentAdapter(sampleComments)
        binding.rvComments.layoutManager = LinearLayoutManager(this)
        binding.rvComments.adapter = commentAdapter

        // Xử lý sự kiện gửi bình luận
        binding.btnSendComment.setOnClickListener {
            val newComment = binding.etComment.text.toString().trim()
            if (newComment.isNotEmpty()) {
                // Ở đây bạn có thể thêm vào danh sách, gọi API...
                // Ví dụ thêm vào Adapter cho hiển thị tạm thời:
                val updatedList = commentAdapter.getData().toMutableList()
                updatedList.add(
                    CommentItem("Hải Nam", "09:41", newComment)
                )
                commentAdapter.updateData(updatedList)

                // Xóa nội dung EditText
                binding.etComment.setText("")
            }
        }
    }
}
