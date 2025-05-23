package com.example.safeaid.screens.community

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.androidtraining.databinding.ActivityImageViewerBinding
import com.github.chrisbanes.photoview.PhotoView

class ImageViewerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageViewerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        // Get image URL from intent
        val imageUrl = intent.getStringExtra("image_url")

        if (imageUrl != null) {
            // Load the image with Glide
            Glide.with(this)
                .load(imageUrl)
                .into(binding.photoView)
        } else {
            finish() // Close activity if no image URL
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}