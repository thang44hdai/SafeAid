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

        // Quay lại màn hình đăng nhập khi nhấn nút Back
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        // Chuyển về màn hình đăng nhập khi nhấn vào "Đã có tài khoản"
        binding.tvHaveAccount.setOnClickListener {
            onBackPressed()
        }

        // Theo dõi trạng thái đăng ký
        vm.state.observe(this) { state ->
            when (state) {
                is RegisterState.Loading -> {
                    binding.btnRegister.isEnabled = false
                    binding.btnRegister.text = "Đang xử lý..."
                    Toast.makeText(this, "Đang xử lý...", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(this, state.error, Toast.LENGTH_LONG).show()
                }
                is RegisterState.Idle -> {
                    binding.btnRegister.isEnabled = true
                    binding.btnRegister.text = "Đăng ký"
                }
            }
        }

        // Xử lý sự kiện nút Đăng ký
        binding.btnRegister.setOnClickListener {
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
            
            // Validate thông tin đăng ký
            if (username.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            if (password != confirm) {
                Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            // Gọi API đăng ký
            vm.register(username = username, email = email, phone = phone, password = password)
        }
    }
    
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}