package com.example.safeaid.core.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.edit
import com.example.safeaid.core.base.ApiCaller
import com.example.safeaid.core.response.UserInfo
import com.example.safeaid.core.service.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserManager @Inject constructor(
    private val apiService: ApiService
) {
    companion object {
        private const val PREFS_NAME = "user_profile_prefs"
        private const val KEY_USERNAME = "username"
        private const val KEY_AVATAR_URL = ""
        private const val KEY_USER_ID = "user_id"
        private const val KEY_EMAIL = "email"
        private const val KEY_PHONE = "phone_number"
        private const val KEY_ROLE = "role"
        private const val KEY_PROFILE_LOADED = "profile_loaded"
    }

    // Save user info to SharedPreferences
    fun saveUserInfo(context: Context, userInfo: UserInfo) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            putString(KEY_USER_ID, userInfo.userId)
            putString(KEY_USERNAME, userInfo.username)
            putString(KEY_EMAIL, userInfo.email)
            putString(KEY_PHONE, userInfo.phoneNumber)
            putString(KEY_ROLE, userInfo.role)
            putString(KEY_AVATAR_URL, userInfo.profileImagePath)
            putBoolean(KEY_PROFILE_LOADED, true)
        }

        Log.d("UserManager", "User info saved: ${userInfo.username}, ${userInfo.email}, ${userInfo.phoneNumber}, ${userInfo.role}, ${userInfo.profileImagePath}")
    }

    // Get username from SharedPreferences
    fun getUsername(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_USERNAME, "") ?: ""
    }

    // Get avatar URL from SharedPreferences
    fun getAvatarUrl(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_AVATAR_URL, "") ?: ""
    }

    // Update avatar URL in SharedPreferences
    fun updateAvatarUrl(context: Context, avatarUrl: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            putString(KEY_AVATAR_URL, avatarUrl)
        }
    }

    // Get user ID from SharedPreferences
    fun getUserId(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_USER_ID, "") ?: ""
    }

    // Get email from SharedPreferences
    fun getEmail(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_EMAIL, "") ?: ""
    }

    // Get phone from SharedPreferences
    fun getPhoneNumber(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_PHONE, "") ?: ""
    }

    // Get role from SharedPreferences
    fun getRole(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_ROLE, "") ?: ""
    }

    // Check if profile is loaded
    fun isProfileLoaded(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_PROFILE_LOADED, false)
    }

    // Clear all user data (for logout)
    fun clearUserData(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            clear()
        }
    }

    // Fetch user info from API
    suspend fun fetchUserInfo(context: Context, onComplete: (Boolean) -> Unit = {}) {
        val token = Prefs.getToken(context)
        if (token.isNullOrEmpty()) {
            onComplete(false)
            return
        }

        ApiCaller.safeApiCall(
            apiCall = {
                apiService.getUserInfo("Bearer $token")
            },
            callback = { result ->
                result.doIfSuccess { response ->
                    Log.d("UserManager", "User info : ${response.user}" )
                    saveUserInfo(context, response.user)
                    onComplete(true)
                }
                result.doIfFailure {
                    onComplete(false)
                }
            }
        )
    }

    // For convenience - loads user profile if not loaded yet
    fun ensureProfileLoaded(context: Context, onComplete: (Boolean) -> Unit) {
        if (isProfileLoaded(context)) {
            onComplete(true)
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val success = fetchUserInfo(context)
            withContext(Dispatchers.Main) {
                onComplete(success)
            }
        }
    }

    private fun CoroutineScope.onComplete(unit: Any) {}
}