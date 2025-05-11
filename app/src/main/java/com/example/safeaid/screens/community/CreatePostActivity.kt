package com.example.safeaid.screens.community

import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidtraining.R
import com.example.androidtraining.databinding.ActivityCreatePostBinding

class CreatePostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreatePostBinding
    private var selectedImageUri: Uri? = null

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                binding.flImageContainer.visibility = View.VISIBLE
                binding.ivPhotoPreview.setImageURI(it)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val sys = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom)
            insets
        }

        // Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        // Chọn ảnh
        binding.btnAddImage.setOnClickListener {
            pickImage.launch("image/*")
        }

        // Xóa ảnh
        binding.btnRemovePhoto.setOnClickListener {
            selectedImageUri = null
            binding.flImageContainer.visibility = View.GONE
        }

        // Zoom ảnh khi click vào preview
        binding.ivPhotoPreview.setOnClickListener {
            selectedImageUri?.let { uri ->
                // TODO: show full-screen preview/dialog
                Toast.makeText(this, "Zoom ảnh: $uri", Toast.LENGTH_SHORT).show()
            }
        }

        // Đăng bài
        binding.toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_publish) {
                publishPost()
                true
            } else false
        }
    }

    private fun publishPost() {
        val content = binding.etContent.text.toString().trim()
        if (content.isEmpty()) {
            Toast.makeText(this, "Bạn chưa nhập nội dung", Toast.LENGTH_SHORT).show()
            return
        }
        // TODO: upload selectedImageUri nếu có, lấy URL rồi gọi ViewModel/Repo
        Toast.makeText(
            this,
            "Đang đăng...\nNội dung: $content\nẢnh: ${selectedImageUri?.lastPathSegment ?: "Không có"}",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> { finish(); true }
            else -> super.onOptionsItemSelected(item)
        }
}
