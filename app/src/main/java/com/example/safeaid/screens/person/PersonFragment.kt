package com.example.safeaid.screens.person

import android.content.Intent
import com.example.safeaid.core.ui.BaseFragment
import com.example.androidtraining.databinding.FragmentFingerBinding
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
    }
}