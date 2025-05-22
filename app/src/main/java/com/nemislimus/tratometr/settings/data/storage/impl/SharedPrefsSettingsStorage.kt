package com.nemislimus.tratometr.settings.data.storage.impl

import android.content.Context
import com.nemislimus.tratometr.settings.data.storage.SettingsStorage
import com.nemislimus.tratometr.settings.data.storage.model.SettingsParamsDto

class SharedPrefsSettingsStorage(context: Context): SettingsStorage {

    private val sharedPrefs = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)

    override suspend fun get(): SettingsParamsDto {
        val time = sharedPrefs.getString(REMINDER_TIME_KEY, DEFAULT_TIME) ?: DEFAULT_TIME
        val isDarkMode = sharedPrefs.getBoolean(IS_DARK_MODE_KEY, false)
        return SettingsParamsDto(time, isDarkMode)
    }

    override suspend fun save(params: SettingsParamsDto) {
        sharedPrefs.edit()
            .putString(REMINDER_TIME_KEY, params.remindTime)
            .putBoolean(IS_DARK_MODE_KEY, params.isDarkMode)
            .apply()
    }

    companion object {
        private const val SHARED_PREF_NAME = "settings_storage"
        private const val IS_DARK_MODE_KEY = "is_dark_mode"
        private const val REMINDER_TIME_KEY = "reminder"
        private const val DEFAULT_TIME = ""

    }
}