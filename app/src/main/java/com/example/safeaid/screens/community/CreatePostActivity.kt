// java/com/example/safeaid/screens/community/CreatePostActivity.kt
package com.example.safeaid.screens.community

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.androidtraining.R
import com.example.androidtraining.databinding.ActivityCreatePostBinding
import com.example.safeaid.core.utils.Prefs
import com.example.safeaid.core.utils.UserManager
import com.example.safeaid.screens.community.viewmodel.CreatePostState
import com.example.safeaid.screens.community.viewmodel.CreatePostViewModel
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MultipartBody
import javax.inject.Inject

@AndroidEntryPoint
class CreatePostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreatePostBinding
    private val viewModel: CreatePostViewModel by viewModels()
    private var selectedImageUri: Uri? = null

    @Inject
    lateinit var userManager: UserManager

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.also {
                selectedImageUri = it
                binding.flImageContainer.visibility = View.VISIBLE
                binding.ivPhotoPreview.setImageURI(it)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        // edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val sys = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom)
            insets
        }

        // toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        // Display current user info
        val username = userManager.getUsername(this)
        binding.tvUsername.text = if (username.isNotEmpty()) username else "Bạn"

        // Load user avatar if available
        val avatarUrl = userManager.getAvatarUrl(this)
        if (avatarUrl.isNotEmpty()) {
            Glide.with(this)
                .load(avatarUrl)
                .placeholder(R.drawable.default_avt)
                .error(R.drawable.default_avt)
                .into(binding.ivAvatar)
        }

        // image pick / remove
        binding.btnAddImage.setOnClickListener { pickImage.launch("image/*") }
        binding.btnRemovePhoto.setOnClickListener {
            selectedImageUri = null
            binding.flImageContainer.visibility = View.GONE
        }

        // Zoom
        binding.ivPhotoPreview.setOnClickListener {
            selectedImageUri?.let { uri ->
                // show full screen preview...
                Toast.makeText(this, "Zoom ảnh: $uri", Toast.LENGTH_SHORT).show()
            }
        }

        // observe ViewModel
        viewModel.state.observe(this) { s ->
            when (s) {
                is CreatePostState.Loading -> binding.progressBar.visibility = View.VISIBLE
                is CreatePostState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, s.response.message, Toast.LENGTH_SHORT).show()
                    val data = Intent().apply {
                        putExtra("new_post_id", s.response.post.post_id)
                    }
                    setResult(Activity.RESULT_OK, data)
                    finish()
                }
                is CreatePostState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, s.message, Toast.LENGTH_LONG).show()
                }
                else -> Unit
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_post, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> { finish(); true }
        R.id.action_publish -> {
            publishPost()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun publishPost() {
        val contentText = binding.etContent.text.toString().trim()
        if (contentText.isEmpty()) {
            Toast.makeText(this, "Bạn chưa nhập nội dung", Toast.LENGTH_SHORT).show()
            return
        }



        // 1) Build RequestBody for text
        val contentRb = contentText
            .toRequestBody("text/plain".toMediaType())

        // no title in this screen
        val titleRb: okhttp3.RequestBody? = null

        // 2) Build MultipartBody.Part for the single image (if any)
        val parts = selectedImageUri?.let { uri ->
            contentResolver.openInputStream(uri)?.use { stream ->
                val bytes = stream.readBytes()
                val mime  = contentResolver.getType(uri) ?: "image/*"
                val rb    = bytes.toRequestBody(mime.toMediaType())
                listOf(
                    MultipartBody.Part.createFormData("images", "upload.jpg", rb)
                )
            }
        } ?: emptyList()

        // 4) Call ViewModel
        viewModel.createPost(contentRb, titleRb, parts)
    }
}
