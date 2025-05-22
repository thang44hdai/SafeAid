package com.example.safeaid.screens.person

import android.app.Activity
import android.content.Intent
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.safeaid.core.ui.BaseFragment
import com.example.androidtraining.databinding.FragmentFingerBinding
import com.example.safeaid.core.utils.Prefs
import com.example.safeaid.screens.authenticate.LoginActivity
import com.example.safeaid.screens.changepassword.ChangePasswordActivity
import com.example.safeaid.screens.editprofile.EditProfileActivity
import com.example.safeaid.screens.person.viewmodel.AvatarUploadState
import com.example.safeaid.screens.person.viewmodel.UpdateAvatarViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PersonFragment : BaseFragment<FragmentFingerBinding>(){
    private val viewModel: UpdateAvatarViewModel by viewModels()

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
                    viewBinding.ivAvatar.setImageURI(state.uri)
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
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        getContent.launch(intent)
    }

    private fun showLoading(isLoading: Boolean) {
        // Implement loading indicator logic
        // For example, if you have a progress bar in your layout:
        // viewBinding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}