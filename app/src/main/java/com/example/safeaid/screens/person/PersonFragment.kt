package com.example.safeaid.screens.person

import android.content.Intent
import com.example.safeaid.core.ui.BaseFragment
import com.example.androidtraining.databinding.FragmentFingerBinding
import com.example.safeaid.core.utils.Prefs
import com.example.safeaid.screens.authenticate.LoginActivity
import com.example.safeaid.screens.changepassword.ChangePasswordActivity
import com.example.safeaid.screens.editprofile.EditProfileActivity

class PersonFragment : BaseFragment<FragmentFingerBinding>(){
    override fun isHostFragment(): Boolean {
        return true
    }

    override fun onInit() {
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
}