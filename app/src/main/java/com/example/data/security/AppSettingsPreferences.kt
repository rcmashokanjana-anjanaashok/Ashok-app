package com.example.data.security

import android.content.Context
import android.content.SharedPreferences

class AppSettingsPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("daily_life_app_prefs", Context.MODE_PRIVATE)

    companion object {
        // Profile Info
        private const val KEY_USER_NAME = "pref_user_name"
        private const val KEY_USER_EMAIL = "pref_user_email"
        private const val KEY_USER_PHONE = "pref_user_phone"
        private const val KEY_PROFILE_PHOTO_URI = "pref_profile_photo_uri"
        private const val KEY_AVATAR_PRESET = "pref_avatar_preset"

        // Security
        private const val KEY_PIN_ENABLED = "pin_lock_enabled"
        private const val KEY_PIN_CODE = "pin_lock_code"
        private const val KEY_MASK_BALANCE = "pref_mask_balance"

        // Notification Toggles
        private const val KEY_DAILY_REMINDER = "pref_daily_reminder"
        private const val KEY_BILL_ALERTS = "pref_bill_alerts"
        private const val KEY_TASK_REMINDERS = "pref_task_reminders"
        private const val KEY_BUDGET_ALERTS = "pref_budget_alerts"

        // Display & UX Toggles
        private const val KEY_THEME_MODE = "pref_theme_mode" // "SYSTEM", "LIGHT", "DARK"
        private const val KEY_HAPTIC = "pref_haptic_feedback"
        private const val KEY_COMPACT_NUMBERS = "pref_compact_numbers"
        private const val KEY_SHOW_DECIMALS = "pref_show_decimals"
        private const val KEY_CURRENCY_SYMBOL = "pref_currency_symbol"
    }

    // Profile getters & setters
    var userName: String
        get() = prefs.getString(KEY_USER_NAME, "Ashok Anjana") ?: "Ashok Anjana"
        set(value) = prefs.edit().putString(KEY_USER_NAME, value).apply()

    var userEmail: String
        get() = prefs.getString(KEY_USER_EMAIL, "rcmashokanjana@gmail.com") ?: "rcmashokanjana@gmail.com"
        set(value) = prefs.edit().putString(KEY_USER_EMAIL, value).apply()

    var userPhone: String
        get() = prefs.getString(KEY_USER_PHONE, "+91 98765 43210") ?: "+91 98765 43210"
        set(value) = prefs.edit().putString(KEY_USER_PHONE, value).apply()

    var profilePhotoUri: String?
        get() = prefs.getString(KEY_PROFILE_PHOTO_URI, null)
        set(value) = prefs.edit().putString(KEY_PROFILE_PHOTO_URI, value).apply()

    var avatarPreset: Int
        get() = prefs.getInt(KEY_AVATAR_PRESET, 0)
        set(value) = prefs.edit().putInt(KEY_AVATAR_PRESET, value).apply()

    // Security
    var isPinEnabled: Boolean
        get() = prefs.getBoolean(KEY_PIN_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_PIN_ENABLED, value).apply()

    var savedPin: String?
        get() = prefs.getString(KEY_PIN_CODE, null)
        set(value) = prefs.edit().putString(KEY_PIN_CODE, value).apply()

    var isMaskBalanceEnabled: Boolean
        get() = prefs.getBoolean(KEY_MASK_BALANCE, false)
        set(value) = prefs.edit().putBoolean(KEY_MASK_BALANCE, value).apply()

    // Notification Toggles
    var isDailyReminderEnabled: Boolean
        get() = prefs.getBoolean(KEY_DAILY_REMINDER, true)
        set(value) = prefs.edit().putBoolean(KEY_DAILY_REMINDER, value).apply()

    var isBillAlertEnabled: Boolean
        get() = prefs.getBoolean(KEY_BILL_ALERTS, true)
        set(value) = prefs.edit().putBoolean(KEY_BILL_ALERTS, value).apply()

    var isTaskReminderEnabled: Boolean
        get() = prefs.getBoolean(KEY_TASK_REMINDERS, true)
        set(value) = prefs.edit().putBoolean(KEY_TASK_REMINDERS, value).apply()

    var isBudgetAlertEnabled: Boolean
        get() = prefs.getBoolean(KEY_BUDGET_ALERTS, true)
        set(value) = prefs.edit().putBoolean(KEY_BUDGET_ALERTS, value).apply()

    // UX & Display Toggles
    var themeMode: String
        get() = prefs.getString(KEY_THEME_MODE, "SYSTEM") ?: "SYSTEM"
        set(value) = prefs.edit().putString(KEY_THEME_MODE, value).apply()

    var isHapticFeedbackEnabled: Boolean
        get() = prefs.getBoolean(KEY_HAPTIC, true)
        set(value) = prefs.edit().putBoolean(KEY_HAPTIC, value).apply()

    var isCompactNumbersEnabled: Boolean
        get() = prefs.getBoolean(KEY_COMPACT_NUMBERS, false)
        set(value) = prefs.edit().putBoolean(KEY_COMPACT_NUMBERS, value).apply()

    var isDecimalPlacesEnabled: Boolean
        get() = prefs.getBoolean(KEY_SHOW_DECIMALS, true)
        set(value) = prefs.edit().putBoolean(KEY_SHOW_DECIMALS, value).apply()

    var currencySymbol: String
        get() = prefs.getString(KEY_CURRENCY_SYMBOL, "₹") ?: "₹"
        set(value) = prefs.edit().putString(KEY_CURRENCY_SYMBOL, value).apply()

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
