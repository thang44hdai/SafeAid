package com.example.safeaid.core.ui

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide

class ImageZoomDialog(private val imageUrl: String) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val imageView = ImageView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            scaleType = ImageView.ScaleType.FIT_CENTER
            setBackgroundColor(Color.BLACK)
        }

        Glide.with(this)
            .load(imageUrl)
            .into(imageView)

        return AlertDialog.Builder(requireContext())
            .setView(imageView)
            .setCancelable(true)
            .create()
    }
}
