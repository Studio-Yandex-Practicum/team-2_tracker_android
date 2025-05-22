package com.nemislimus.tratometr.settings.data.storage

import com.nemislimus.tratometr.settings.data.storage.model.SettingsParamsDto

interface SettingsStorage {
    suspend fun get(): SettingsParamsDto
    suspend fun save(params: SettingsParamsDto)
}