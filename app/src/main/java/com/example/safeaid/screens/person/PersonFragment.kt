package com.example.safeaid.screens.person

import android.R
import android.app.Activity
import android.content.Intent
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.safeaid.core.ui.BaseFragment
import com.example.androidtraining.databinding.FragmentFingerBinding
import com.example.safeaid.core.utils.Prefs
import com.example.safeaid.core.utils.UserManager
import com.example.safeaid.screens.authenticate.LoginActivity
import com.example.safeaid.screens.changepassword.ChangePasswordActivity
import com.example.safeaid.screens.editprofile.EditProfileActivity
import com.example.safeaid.screens.person.viewmodel.AvatarUploadState
import com.example.safeaid.screens.person.viewmodel.UpdateAvatarViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PersonFragment : BaseFragment<FragmentFingerBinding>(){
    private val viewModel: UpdateAvatarViewModel by viewModels()

    @Inject
    lateinit var userManager: UserManager

    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                // Call the ViewModel to handle the upload
                viewModel.uploadAvatar(requireContext(), uri)
            }
        }
    }

    override fun isHostFragment(): Boolean {
        return true
    }

    override fun onInit() {
        // Load user profile when fragment starts
        userManager.ensureProfileLoaded(requireContext()) { success ->
            if (success) {
                updateProfileUi()
            }
        }

        // Observe upload state changes
        viewModel.uploadState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AvatarUploadState.Loading -> {
                    // Show loading indicator
                    showLoading(true)
                }
                is AvatarUploadState.Success -> {
                    // Update UI with new avatar
                    showLoading(false)
                    // Refresh UI with latest data
                    updateProfileUi()
                    Toast.makeText(requireContext(), "Avatar đã được cập nhật thành công", Toast.LENGTH_SHORT).show()
                }
                is AvatarUploadState.Error -> {
                    // Show error message
                    showLoading(false)
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onInitObserver() {
    }

    override fun onInitListener() {
        viewBinding.btnEditInfo.setOnClickListener {
            // Chuyển sang màn EditProfile
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            startActivity(intent)
        }

        viewBinding.optionChangePass.setOnClickListener{
            val intent = Intent(requireContext(), ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        viewBinding.btnEditAvatar.setOnClickListener {
            openImagePicker()
        }

        // --- Thêm nút Đăng xuất ---
        viewBinding.optionLogout.setOnClickListener {
            // 1) Xoá token
            Prefs.clearToken(requireContext())

            // 2) Chạy LoginActivity và clear backstack để không thể back về màn trước
            val intent = Intent(requireContext(), LoginActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            startActivity(intent)
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png", "image/gif", "image/webp"))
        }
        getContent.launch(intent)
    }

    private fun updateProfileUi() {
        // Update username from UserManager
        viewBinding.tvName.text = "@" + userManager.getUsername(requireContext())

        // Update avatar using Glide
        val avatarUrl = userManager.getAvatarUrl(requireContext())
        if (avatarUrl.isNotEmpty()) {
            Glide.with(requireContext())
                .load(avatarUrl)
                .placeholder(com.example.androidtraining.R.drawable.default_avt)
                .error(com.example.androidtraining.R.drawable.default_avt)
                .circleCrop()
                .into(viewBinding.ivAvatar)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        // Implement loading indicator logic
        // For example, if you have a progress bar in your layout:
        // viewBinding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }


}