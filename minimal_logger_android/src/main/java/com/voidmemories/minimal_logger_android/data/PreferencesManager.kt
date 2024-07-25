package com.voidmemories.minimal_logger_android.data

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Application) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "minimal_logger_pref"
    }

    fun getString(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    fun saveString(key: String, value: String) {
        with(sharedPreferences.edit()) {
            putString(key, value)
            apply()
        }
    }
}
