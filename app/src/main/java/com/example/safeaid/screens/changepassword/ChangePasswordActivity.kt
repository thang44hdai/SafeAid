package com.example.safeaid.screens.changepassword

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.androidtraining.R
import com.example.androidtraining.databinding.ActivityChangePasswordBinding
import com.example.safeaid.core.utils.DataResult
import com.example.safeaid.screens.changepassword.viewmodel.ChangePasswordEvent
import com.example.safeaid.screens.changepassword.viewmodel.ChangePasswordState
import com.example.safeaid.screens.changepassword.viewmodel.ChangePasswordViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    private val viewModel: ChangePasswordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the toolbar with back navigation
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Set up back button click listener
        toolbar.setNavigationOnClickListener {
            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up change password button
        binding.btnStart.setOnClickListener {
            changePassword()
        }

        // Observe state changes
        lifecycleScope.launch {
            viewModel.viewState.collect { result ->
                handleState(result)
            }
        }
    }

    private fun handleState(result: DataResult<ChangePasswordState>?) {
        if (result == null) return

        when (result) {
            is DataResult.Loading -> {
                binding.btnStart.isEnabled = false
                binding.btnStart.text = "Đang xử lý..."
            }
            is DataResult.Success -> {
                val state = result.data
                when (state) {
                    is ChangePasswordState.Success -> {
                        binding.btnStart.isEnabled = true
                        binding.btnStart.text = "Đổi mật khẩu"
                        Toast.makeText(this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    is ChangePasswordState.Error -> {
                        binding.btnStart.isEnabled = true
                        binding.btnStart.text = "Đổi mật khẩu"
                        Toast.makeText(this, "Lỗi: ${state.message}", Toast.LENGTH_SHORT).show()
                    }
                    is ChangePasswordState.Loading -> {
                        binding.btnStart.isEnabled = false
                        binding.btnStart.text = "Đang xử lý..."
                    }
                }
            }
            is DataResult.Error -> {
                binding.btnStart.isEnabled = true
                binding.btnStart.text = "Đổi mật khẩu"
                Toast.makeText(this, "Lỗi: ${result.error}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun changePassword() {
        val oldPassword = binding.etOldPassword.text.toString().trim()
        val newPassword = binding.etNewPassword.text.toString().trim()
        val confirmNewPassword = binding.etCFNewPassword.text.toString().trim()

        // Validate inputs
        if (oldPassword.isEmpty()) {
            binding.etOldPassword.error = "Vui lòng nhập mật khẩu cũ"
            return
        }

        if (newPassword.isEmpty()) {
            binding.etNewPassword.error = "Vui lòng nhập mật khẩu mới"
            return
        }

        if (confirmNewPassword.isEmpty()) {
            binding.etCFNewPassword.error = "Vui lòng xác nhận mật khẩu mới"
            return
        }

        if (newPassword != confirmNewPassword) {
            binding.etCFNewPassword.error = "Mật khẩu xác nhận không khớp"
            return
        }

        // Change password
        viewModel.onTriggerEvent(ChangePasswordEvent.ChangePassword(oldPassword, newPassword))
    }
}