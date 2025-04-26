package com.example.safeaid.screens.news

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androidtraining.databinding.ActivityAddNewsBinding

class AddNewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Hiển thị bàn phím ngay khi mở Activity
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        // 2. Cấu hình Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        // 3. Cấu hình RichEditor
        binding.editor.apply {
            setEditorHeight(200)                       // chiều cao khung ban đầu
            setEditorFontSize(16)                     // kích cỡ text mặc định
            setEditorFontColor(Color.BLACK)           // màu chữ
            setPadding(16, 16, 16, 16)                // padding bên trong
            setPlaceholder("Nhập nội dung bài báo...")
            setInputEnabled(true)                     // bật nhập liệu
            requestFocus()                            // focus vào editor
        }

        // 4. Nạp nội dung cũ nếu đang edit
        intent.getStringExtra("news_html")?.let { html ->
            binding.editor.html = html
        }

        // 5. Xử lý nhấn Đăng bài
        binding.btnPublish.setOnClickListener {
            val finalHtml = binding.editor.html ?: ""
            // TODO: gọi API gửi finalHtml lên server
            Toast.makeText(this, "HTML bạn vừa soạn:\n$finalHtml", Toast.LENGTH_LONG).show()
        }
    }

    // Các hàm onClick cho thanh công cụ:
    fun onBold(view: View) {
        binding.editor.setBold()
    }
    fun onItalic(view: View) {
        binding.editor.setItalic()
    }
    fun onUnderline(view: View) {
        binding.editor.setUnderline()
    }
    fun onStrikethrough(view: View) {
        binding.editor.setStrikeThrough()
    }
    fun onHeading1(view: View) {
        binding.editor.setHeading(1)
    }
    fun onHeading2(view: View) {
        binding.editor.setHeading(2)
    }
    fun onHeading3(view: View) {
        binding.editor.setHeading(3)
    }
    // TODO: Thêm onClick cho subscript, superscript, insertImage(), v.v.
}
