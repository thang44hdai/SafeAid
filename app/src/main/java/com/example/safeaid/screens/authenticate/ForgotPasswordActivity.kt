package com.example.safeaid.screens.authenticate

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidtraining.R
import com.example.androidtraining.databinding.ActivityForgotPasswordBinding
import com.example.safeaid.screens.authenticate.viewmodel.ForgotPasswordState
import com.example.safeaid.screens.authenticate.viewmodel.ForgotPasswordViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private val viewModel: ForgotPasswordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val sys = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom)
            insets
        }

        setupInputValidation()
        setupClickListeners()
        observeViewModel()
    }
    
    private fun setupInputValidation() {
        // Xóa thông báo lỗi khi người dùng bắt đầu nhập lại
        binding.etEmail.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.tilEmail.error = null
            }
        }
    }

    private fun setupClickListeners() {
        // Quay lại màn hình đăng nhập
        binding.btnBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        binding.tvBackToLogin.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        // Xử lý khi nhấn nút đặt lại mật khẩu
        binding.btnResetPassword.setOnClickListener {
            // Xóa thông báo lỗi cũ
            binding.tilEmail.error = null
            
            val email = binding.etEmail.text.toString().trim()
            viewModel.resetPassword(email)
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(this) { state ->
            when (state) {
                is ForgotPasswordState.Idle -> {
                    // Không làm gì
                }
                is ForgotPasswordState.Loading -> {
                    showLoading(true)
                }
                is ForgotPasswordState.Success -> {
                    showLoading(false)
                    showSuccessUI(state.message)
                }
                is ForgotPasswordState.Error -> {
                    showLoading(false)
                    handleError(state.message)
                }
            }
        }
    }
    
    private fun handleError(errorMessage: String) {
        when {
            errorMessage.contains("Email không được để trống") -> {
                binding.tilEmail.error = errorMessage
            }
            errorMessage.contains("Email không hợp lệ") -> {
                binding.tilEmail.error = errorMessage
            }
            errorMessage.contains("Không tìm thấy email") -> {
                binding.tilEmail.error = errorMessage
                showErrorDialog("Email không tồn tại", errorMessage)
            }
            else -> {
                // Lỗi khác hiển thị thông qua Toast và có thể dialog
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                showErrorDialog("Không thể gửi email", errorMessage)
            }
        }
    }
    
    private fun showErrorDialog(title: String, message: String) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.btnResetPassword.isEnabled = false
            binding.btnResetPassword.text = "Đang xử lý..."
        } else {
            binding.btnResetPassword.isEnabled = true
            binding.btnResetPassword.text = "Đặt lại mật khẩu"
        }
    }

    private fun showSuccessUI(message: String) {
        // Hiển thị giao diện thành công
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        
        // Thay đổi UI khi gửi thành công
        binding.tvTitle.text = "Đã gửi email!"
        binding.tvSubtitle.text = "Vui lòng kiểm tra hộp thư của bạn"
        binding.cardResetPassword.visibility = View.GONE
        
        // Hiển thị giao diện thành công
        binding.ivIllustration.setImageResource(R.drawable.ic_success)
        binding.ivIllustration.alpha = 1.0f
        binding.ivIllustration.setBackgroundResource(R.drawable.bg_success)
        binding.ivIllustration.setPadding(48, 48, 48, 48)
        
        // Hiển thị nút Quay lại đăng nhập
        binding.tvBackToLogin.visibility = View.VISIBLE
        binding.tvBackToLogin.textSize = 16f
        binding.tvBackToLogin.setPadding(32, 16, 32, 16)
        binding.tvBackToLogin.setBackgroundResource(R.drawable.bg_success)
        
        // Hiển thị dialog thông báo thành công
        showSuccessDialog(message)
        
        // Tự động quay lại màn hình đăng nhập sau 3 giây
        binding.root.postDelayed({
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }, 5000) // Tăng thời gian chờ lên 5 giây
    }
    
    private fun showSuccessDialog(message: String) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Đã gửi email!")
        builder.setMessage(message)
        builder.setPositiveButton("Đóng") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
} 