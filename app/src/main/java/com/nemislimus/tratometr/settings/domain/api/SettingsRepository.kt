package com.nemislimus.tratometr.settings.domain.api

import com.nemislimus.tratometr.settings.domain.model.SettingsParams

interface SettingsRepository {
    suspend fun saveSettings(params: SettingsParams)
    suspend fun getSettings(): SettingsParams
}