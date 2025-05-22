package com.nemislimus.tratometr.settings.domain

import com.nemislimus.tratometr.settings.domain.api.SettingsRepository
import javax.inject.Inject

class GetDarkModeValueUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend fun execute(): Boolean {
        return repository.getSettings().isDarkMode
    }
}