package com.example.data.security

import android.content.Context
import android.content.SharedPreferences

class SecurityPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("daily_life_security_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_PIN_ENABLED = "pin_lock_enabled"
        private const val KEY_PIN_CODE = "pin_lock_code"
    }

    var isPinEnabled: Boolean
        get() = prefs.getBoolean(KEY_PIN_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_PIN_ENABLED, value).apply()

    var savedPin: String?
        get() = prefs.getString(KEY_PIN_CODE, null)
        set(value) = prefs.edit().putString(KEY_PIN_CODE, value).apply()

    fun verifyPin(input: String): Boolean {
        val current = savedPin ?: return true
        return current == input
    }

    fun setPin(pin: String) {
        savedPin = pin
        isPinEnabled = true
    }

    fun disablePin() {
        isPinEnabled = false
        savedPin = null
    }
}
