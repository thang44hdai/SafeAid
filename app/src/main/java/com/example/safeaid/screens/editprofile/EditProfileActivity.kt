package com.example.safeaid.screens.editprofile

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.asLiveData
import com.example.androidtraining.R
import com.example.androidtraining.databinding.ActivityEditProfileBinding
import com.example.safeaid.core.utils.DataResult
import com.example.safeaid.screens.editprofile.viewmodel.EditProfileEvent
import com.example.safeaid.screens.editprofile.viewmodel.EditProfileState
import com.example.safeaid.screens.editprofile.viewmodel.EditProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private val viewModel: EditProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
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

        // Load current user data
        val oldUserData = loadUserData()
        Log.d("EditProfileActivity", "Current user data: $oldUserData")
//        Current user data: kotlin.Unit


        // Set up update button
        binding.btnStart.setOnClickListener {
            updateProfile()
        }

        viewModel.viewState.asLiveData().observe(this) { result ->
            handleState(result)
        }

        // Collect viewState (not using observe)
        collectViewState()
    }

    private fun collectViewState() {
        // This is where you'd normally collect Flow or observe LiveData
        // Since I don't know the exact pattern used in your BaseViewModel,
        // I'm making an educated guess based on CommunityViewModel:

        // Let viewModel handle state updates - it's already collecting viewState
        viewModel.onTriggerEvent(EditProfileEvent.LoadUserData)
    }

    private fun handleState(result: DataResult<EditProfileState>?) {
        when (result) {
            is DataResult.Loading -> {
                binding.btnStart.isEnabled = false
                binding.btnStart.text = "Đang cập nhật..."
            }
            is DataResult.Success -> {
                val state = result.data
                when (state) {
                    is EditProfileState.Success -> {
                        binding.btnStart.isEnabled = true
                        binding.btnStart.text = "Cập nhật"
                        Toast.makeText(this, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    is EditProfileState.Error -> {
                        binding.btnStart.isEnabled = true
                        binding.btnStart.text = "Cập nhật"
                        Toast.makeText(this, "Lỗi: ${state.message}", Toast.LENGTH_SHORT).show()
                    }
                    is EditProfileState.Loading -> {
                        binding.btnStart.isEnabled = false
                        binding.btnStart.text = "Đang cập nhật..."
                    }
                    is EditProfileState.UserData -> {
                        // Update UI fields with current data
                        binding.etUserName.setText(state.username)
                        binding.etPhonenumber.setText(state.phoneNumber)
                    }
                }
            }
            is DataResult.Error -> {
                binding.btnStart.isEnabled = true
                binding.btnStart.text = "Cập nhật"
                Toast.makeText(this, "Lỗi: ${result.error}", Toast.LENGTH_SHORT).show()
            }

            null -> TODO()
        }
    }

    private fun loadUserData() {
        viewModel.onTriggerEvent(EditProfileEvent.LoadUserData)
    }

    private fun updateProfile() {
        val username = binding.etUserName.text.toString().trim()
        val phoneNumber = binding.etPhonenumber.text.toString().trim()

        // Validate inputs
        if (username.isEmpty()) {
            binding.etUserName.error = "Username không được để trống"
            return
        }

        // Update profile
        viewModel.onTriggerEvent(EditProfileEvent.UpdateProfile(username, phoneNumber))
    }
}