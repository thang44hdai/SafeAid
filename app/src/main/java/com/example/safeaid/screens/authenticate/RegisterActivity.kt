package com.example.safeaid.screens.authenticate

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.androidtraining.R
import com.example.androidtraining.databinding.ActivityRegisterBinding
import com.example.safeaid.screens.authenticate.viewmodel.RegisterState
import com.example.safeaid.screens.authenticate.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val vm: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
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
        binding.etUsername.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilUsername.error = null
        }
        
        binding.etEmail.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilEmail.error = null
        }
        
        binding.etPhone.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilPhone.error = null
        }
        
        binding.etPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilPassword.error = null
        }
        
        binding.etConfirm.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilConfirm.error = null
        }
    }

    private fun setupClickListeners() {
        // Quay lại màn hình đăng nhập khi nhấn nút Back
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        // Chuyển về màn hình đăng nhập khi nhấn vào "Đã có tài khoản"
        binding.tvHaveAccount.setOnClickListener {
            onBackPressed()
        }

        // Xử lý sự kiện nút Đăng ký
        binding.btnRegister.setOnClickListener {
            // Xóa tất cả thông báo lỗi cũ
            clearAllErrors()
            
            val username = binding.etUsername.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val password = binding.etPassword.text.toString()
            val confirm = binding.etConfirm.text.toString()
            
            // Kiểm tra đã đồng ý với điều khoản chưa
            if (!binding.cbTerms.isChecked) {
                Toast.makeText(this, "Vui lòng đồng ý với Điều khoản và Chính sách", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            // Kiểm tra mật khẩu xác nhận có khớp không
            if (password != confirm) {
                binding.tilConfirm.error = "Mật khẩu xác nhận không khớp"
                return@setOnClickListener
            }
            
            // Gọi API đăng ký
            vm.register(username = username, email = email, phone = phone, password = password)
        }
    }
    
    private fun clearAllErrors() {
        binding.tilUsername.error = null
        binding.tilEmail.error = null
        binding.tilPhone.error = null
        binding.tilPassword.error = null
        binding.tilConfirm.error = null
    }

    private fun observeViewModel() {
        // Theo dõi trạng thái đăng ký
        vm.state.observe(this) { state ->
            when (state) {
                is RegisterState.Loading -> {
                    binding.btnRegister.isEnabled = false
                    binding.btnRegister.text = "Đang xử lý..."
                }
                is RegisterState.Success -> {
                    binding.btnRegister.isEnabled = true
                    binding.btnRegister.text = "Đăng ký"
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                    
                    // Chuyển về màn hình đăng nhập sau khi đăng ký thành công
                    finish()
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                }
                is RegisterState.Error -> {
                    binding.btnRegister.isEnabled = true
                    binding.btnRegister.text = "Đăng ký"
                    
                    // Hiển thị lỗi trên trường tương ứng
                    when {
                        state.error.contains("Tên đăng nhập không được để trống") -> {
                            binding.tilUsername.error = state.error
                        }
                        state.error.contains("Email không được để trống") -> {
                            binding.tilEmail.error = state.error
                        }
                        state.error.contains("Email không hợp lệ") -> {
                            binding.tilEmail.error = state.error
                        }
                        state.error.contains("Số điện thoại không được để trống") -> {
                            binding.tilPhone.error = state.error
                        }
                        state.error.contains("Số điện thoại không hợp lệ") -> {
                            binding.tilPhone.error = state.error
                        }
                        state.error.contains("Mật khẩu không được để trống") -> {
                            binding.tilPassword.error = state.error
                        }
                        state.error.contains("Mật khẩu phải có ít nhất") -> {
                            binding.tilPassword.error = state.error
                        }
                        state.error.contains("Email hoặc số điện thoại đã được sử dụng") -> {
                            // Hiển thị lỗi trùng email hoặc số điện thoại
                            binding.tilEmail.error = state.error
                            binding.tilPhone.error = state.error
                            
                            // Hiển thị dialog thông báo
                            showErrorDialog("Thông tin đã tồn tại", state.error)
                        }
                        else -> {
                            // Lỗi khác hiển thị thông qua Toast
                            Toast.makeText(this, state.error, Toast.LENGTH_LONG).show()
                        }
                    }
                }
                is RegisterState.Idle -> {
                    binding.btnRegister.isEnabled = true
                    binding.btnRegister.text = "Đăng ký"
                }
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
    
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}