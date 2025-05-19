package com.example.safeaid.screens.authenticate

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val sys = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom)
            insets
        }

        // nếu đã có tài khoản → quay về Login
        binding.tvHaveAccount.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // observe trạng thái đăng ký
        vm.state.observe(this) { state ->
            when (state) {
                is RegisterState.Loading -> {
                    binding.btnRegister.isEnabled = false
                    Toast.makeText(this, "Đang xử lý...", Toast.LENGTH_SHORT).show()
                }
                is RegisterState.Success -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                    // sau khi đăng ký thành công, chuyển về Login
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                is RegisterState.Error -> {
                    binding.btnRegister.isEnabled = true
                    Toast.makeText(this, state.error, Toast.LENGTH_LONG).show()
                }
                is RegisterState.Idle -> {
                    binding.btnRegister.isEnabled = true
                }
            }
        }

        // khi ấn nút Đăng kí
        binding.btnRegister.setOnClickListener {
            val u = binding.etUsername.text.toString().trim()
            val e = binding.etEmail   .text.toString().trim()
            val p = binding.etPhone   .text.toString().trim()
            val pw= binding.etPassword.text.toString()
            val c = binding.etConfirm .text.toString()
            // validate
            if (u.isEmpty()|| e.isEmpty()|| p.isEmpty()|| pw.isEmpty()|| c.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (pw != c) {
                Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // gọi API
            vm.register(username = u, email = e, phone = p, password = pw)
        }
    }
}