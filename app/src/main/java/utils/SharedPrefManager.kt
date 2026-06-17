package com.example.vcar.utils

import android.content.Context

class SharedPrefManager(context: Context) {

    private val pref =
        context.getSharedPreferences(
            "VCAR_PREF",
            Context.MODE_PRIVATE
        )

    fun saveToken(token: String) {
        pref.edit()
            .putString("TOKEN", token)
            .apply()
    }

    fun getToken(): String? {
        return pref.getString("TOKEN", null)
    }

    fun logout() {
        pref.edit()
            .clear()
            .apply()
    }
}