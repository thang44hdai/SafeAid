package com.example.safeaid.screens.news

import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.androidtraining.R
import com.example.androidtraining.databinding.ActivityAddNewsBinding
import com.example.safeaid.core.network.NewsRepository
import com.example.safeaid.core.service.ApiService
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AddNewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNewsBinding
    private var thumbnailUri: Uri? = null
//    private val repo = NewsRepository(ApiService)

    // Chọn thumbnail
    private val pickThumbnail =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.also {
                thumbnailUri = it
                binding.ivThumbnail.setImageURI(it)
            }
        }

    // Chọn hình chèn vào editor
    private val pickEditorImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.also {
                // TODO: upload lên server, lấy URL rồi:
                // val imageUrl = uploadAndGetUrl(it)
                // binding.editor.insertImage(imageUrl, "Ảnh")
                binding.editor.insertImage(it.toString(), "Ảnh")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mở keyboard ngay
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        // Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        // RichEditor
        binding.editor.apply {
            setEditorHeight(200)
            setEditorFontSize(16)
            setEditorFontColor(0xFF000000.toInt())
            setPadding(16,16,16,16)
            setPlaceholder("Nhập nội dung bài báo…")
            setInputEnabled(true)
            requestFocus()
        }

        // Nếu edit bài cũ
        intent.getStringExtra("news_html")?.let { binding.editor.html = it }

        // Bấm vào thumbnail để chọn
        binding.ivThumbnail.setOnClickListener {
            pickThumbnail.launch("image/*")
        }

        // Bấm chèn hình vào content
        binding.btnInsertPhoto.setOnClickListener {
            pickEditorImage.launch("image/*")
        }

        // Đăng bài
        binding.toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_publish) {
                publish()
                true
            } else false
        }
    }
    private fun publish() {
        val title = binding.etTitle.text.toString().trim()
        val contentHtml = binding.editor.html.orEmpty()
        if (title.isEmpty()) {
            Toast.makeText(this, "Bạn chưa nhập tiêu đề", Toast.LENGTH_SHORT).show()
            return
        }
        // TODO: lấy thumbnailUri, content, gửi lên server
        // 1. Tạo RequestBody cho các trường text
        val titleRb: RequestBody = title
            .toRequestBody("text/plain".toMediaType())
        val contentRb: RequestBody = contentHtml
            .toRequestBody("text/plain".toMediaType())
        // Nếu API yêu cầu thumbnail_path (string), tạm để rỗng
        val thumbnailPathRb: RequestBody = "".toRequestBody("text/plain".toMediaType())

        // 2. Chuyển thumbnailUri thành MultipartBody.Part
        val mediaParts = mutableListOf<MultipartBody.Part>()
        thumbnailUri?.let { uri ->
            contentResolver.openInputStream(uri)?.use { stream ->
                val bytes = stream.readBytes()
                val mime = contentResolver.getType(uri) ?: "image/*"
                val rb = bytes.toRequestBody(mime.toMediaType(), 0, bytes.size)
                // "media" trùng với tên @Part trong ApiService
                val part = MultipartBody.Part.createFormData("media", "thumb.jpg", rb)
                mediaParts.add(part)
            }
        }

        // 3. Gọi API trong coroutine
//        val bearer = "Bearer " + /* lấy token của bạn từ prefs */
//                lifecycleScope.launch {
//                    try {
//                        val resp = repo.postNews(bearer, titleRb, contentRb, thumbnailPathRb, mediaParts)
//                        if (resp.isSuccessful) {
//                            Toast.makeText(this@AddNewsActivity, "Đăng bài thành công!", Toast.LENGTH_SHORT).show()
//                            finish()
//                        } else {
//                            Toast.makeText(this@AddNewsActivity, "Lỗi: ${resp.code()}", Toast.LENGTH_LONG).show()
//                        }
//                    } catch (e: Exception) {
//                        Toast.makeText(this@AddNewsActivity, "Exception: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
//                    }
//                }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> { finish(); true }
        else -> super.onOptionsItemSelected(item)
    }
}
