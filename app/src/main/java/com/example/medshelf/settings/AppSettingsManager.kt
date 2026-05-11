package com.example.medshelf.settings

import android.content.Context

class AppSettingsManager(context: Context) {

    private val preferences = context.getSharedPreferences(
        "medshelf_app_settings",
        Context.MODE_PRIVATE
    )

    fun isAppLockEnabled(): Boolean {
        return preferences.getBoolean(KEY_APP_LOCK_ENABLED, false)
    }

    fun setAppLockEnabled(enabled: Boolean) {
        preferences.edit()
            .putBoolean(KEY_APP_LOCK_ENABLED, enabled)
            .apply()
    }

    fun getPasscode(): String {
        return preferences.getString(KEY_PASSCODE, "") ?: ""
    }

    fun savePasscode(passcode: String) {
        preferences.edit()
            .putString(KEY_PASSCODE, passcode)
            .apply()
    }

    fun hasPasscode(): Boolean {
        return getPasscode().isNotBlank()
    }

    fun clearPasscode() {
        preferences.edit()
            .remove(KEY_PASSCODE)
            .putBoolean(KEY_APP_LOCK_ENABLED, false)
            .apply()
    }

    fun getFontScale(): Float {
        return preferences.getFloat(KEY_FONT_SCALE, 1.0f)
    }

    fun setFontScale(scale: Float) {
        preferences.edit()
            .putFloat(KEY_FONT_SCALE, scale.coerceIn(0.85f, 1.25f))
            .apply()
    }

    companion object {
        private const val KEY_APP_LOCK_ENABLED = "app_lock_enabled"
        private const val KEY_PASSCODE = "passcode"
        private const val KEY_FONT_SCALE = "font_scale"
    }
}