package com.example.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "daily_life_user_settings")

class SettingsDataStore(private val context: Context) {

    companion object {
        val KEY_DARK_MODE = booleanPreferencesKey("dark_mode")
        val KEY_NOTIFICATION_REMINDERS = booleanPreferencesKey("notification_reminders")
        val KEY_BIOMETRIC_LOCK = booleanPreferencesKey("biometric_lock")
        val KEY_CURRENCY_SYMBOL = stringPreferencesKey("currency_symbol")
        val KEY_HAPTIC_FEEDBACK = booleanPreferencesKey("haptic_feedback")
    }

    val isDarkModeFlow: Flow<Boolean> = context.settingsDataStore.data.map { preferences ->
        preferences[KEY_DARK_MODE] ?: false
    }

    val isNotificationRemindersFlow: Flow<Boolean> = context.settingsDataStore.data.map { preferences ->
        preferences[KEY_NOTIFICATION_REMINDERS] ?: true
    }

    val isBiometricLockFlow: Flow<Boolean> = context.settingsDataStore.data.map { preferences ->
        preferences[KEY_BIOMETRIC_LOCK] ?: false
    }

    val isHapticFeedbackFlow: Flow<Boolean> = context.settingsDataStore.data.map { preferences ->
        preferences[KEY_HAPTIC_FEEDBACK] ?: true
    }

    val currencySymbolFlow: Flow<String> = context.settingsDataStore.data.map { preferences ->
        preferences[KEY_CURRENCY_SYMBOL] ?: "₹"
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[KEY_DARK_MODE] = enabled
        }
    }

    suspend fun setNotificationReminders(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[KEY_NOTIFICATION_REMINDERS] = enabled
        }
    }

    suspend fun setBiometricLock(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[KEY_BIOMETRIC_LOCK] = enabled
        }
    }

    suspend fun setHapticFeedback(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[KEY_HAPTIC_FEEDBACK] = enabled
        }
    }

    suspend fun setCurrencySymbol(symbol: String) {
        context.settingsDataStore.edit { preferences ->
            preferences[KEY_CURRENCY_SYMBOL] = symbol
        }
    }
}
