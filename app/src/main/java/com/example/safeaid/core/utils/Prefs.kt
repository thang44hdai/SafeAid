package com.example.safeaid.core.utils

import android.content.Context
import android.content.SharedPreferences

object Prefs {
    private const val NAME = "safe_aid_prefs"
    private const val KEY_TOKEN = ""

    private fun prefs(ctx: Context): SharedPreferences =
        ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE)

    fun saveToken(context: Context, token: String) {
        context
            .getSharedPreferences(NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_TOKEN, token)
            .apply()
    }

    fun getToken(context: Context): String? {
        return context
            .getSharedPreferences(NAME, Context.MODE_PRIVATE)
            .getString(KEY_TOKEN, null)
    }

    fun clearToken(context: Context) {
        context
            .getSharedPreferences(NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_TOKEN)
            .apply()
    }
}