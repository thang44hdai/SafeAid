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
import com.example.androidtraining.databinding.ActivityLoginBinding
import com.example.safeaid.MainActivity
import com.example.safeaid.screens.authenticate.viewmodel.AuthState
import com.example.safeaid.screens.authenticate.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val vm: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
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
        
        binding.etPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.tilPassword.error = null
            }
        }
    }
    
    private fun setupClickListeners() {
        // Set up login button click listener
        binding.btnLogin.setOnClickListener {
            // Xóa các thông báo lỗi cũ
            binding.tilEmail.error = null
            binding.tilPassword.error = null
            
            val email = binding.etEmail.text.toString().trim()
            val pass = binding.etPassword.text.toString().trim()
            vm.login(email, pass)
        }

        // Set up register link click listener
        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        // Set up forgot password link click listener
        binding.tvForgot.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        // Set up social login buttons
        binding.btnGoogle.setOnClickListener {
            Toast.makeText(this, "Đăng nhập bằng Google đang được phát triển", Toast.LENGTH_SHORT).show()
        }

        binding.btnFacebook.setOnClickListener {
            Toast.makeText(this, "Đăng nhập bằng Facebook đang được phát triển", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun observeViewModel() {
        // Observe authentication state
        vm.state.observe(this) { state ->
            when (state) {
                is AuthState.Loading -> {
                    binding.btnLogin.isEnabled = false
                    binding.btnLogin.text = "Đang xử lý..."
                }
                is AuthState.Success -> {
                    binding.btnLogin.isEnabled = true
                    binding.btnLogin.text = "Đăng nhập"
                    Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                    
                    // Chuyển đến MainActivity và xóa activity stack
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                is AuthState.Error -> {
                    binding.btnLogin.isEnabled = true
                    binding.btnLogin.text = "Đăng nhập"
                    
                    // Hiển thị lỗi trên trường tương ứng
                    when {
                        state.message.contains("Email không được để trống") -> {
                            binding.tilEmail.error = state.message
                        }
                        state.message.contains("Email không hợp lệ") -> {
                            binding.tilEmail.error = state.message
                        }
                        state.message.contains("Mật khẩu không được để trống") -> {
                            binding.tilPassword.error = state.message
                        }
                        state.message.contains("Thông tin đăng nhập không hợp lệ") -> {
                            // Hiển thị lỗi thông tin đăng nhập không chính xác
                            binding.tilEmail.error = " "
                            binding.tilPassword.error = state.message
                            
                            // Hiển thị dialog thông báo nếu cần
                            showErrorDialog(state.message)
                        }
                        else -> {
                            // Lỗi khác hiển thị thông qua Toast
                            Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
                else -> { /* Idle */ }
            }
        }
    }

    private fun showErrorDialog(message: String) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Đăng nhập thất bại")
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }
}