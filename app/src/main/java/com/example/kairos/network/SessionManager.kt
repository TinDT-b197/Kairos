package com.example.kairos.network

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences("KAIROS_PREFS", Context.MODE_PRIVATE)

    fun saveUser(userId: Int, userName: String) {
        val editor = prefs.edit()
        editor.putInt("USER_ID", userId)
        editor.putString("USER_NAME", userName)
        editor.apply()
    }

    fun getUserId(): Int = prefs.getInt("USER_ID", -1)

    fun logout() {
        prefs.edit().clear().apply()
    }

}