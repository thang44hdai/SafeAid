package com.example.safeaid.screens.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.safeaid.MainActivity
import com.example.safeaid.core.utils.Prefs
import com.example.safeaid.screens.authenticate.LoginActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val token = Prefs.getToken(this)
        if (token.isNullOrEmpty()) {
            // Chưa login hoặc token trống → vào Login
            startActivity(Intent(this, LoginActivity::class.java))
        } else {
            // Có token → thẳng vào màn Community (hoặc Main)
            startActivity(Intent(this, MainActivity::class.java))
        }
        finish()
    }
}