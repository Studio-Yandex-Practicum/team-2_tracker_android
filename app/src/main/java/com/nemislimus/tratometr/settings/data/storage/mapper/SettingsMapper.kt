package com.nemislimus.tratometr.settings.data.storage.mapper

import com.nemislimus.tratometr.settings.data.storage.model.SettingsParamsDto
import com.nemislimus.tratometr.settings.domain.model.SettingsParams

object SettingsMapper {

    fun map(params: SettingsParams): SettingsParamsDto {
        return SettingsParamsDto(
            remindTime = params.remindTime,
            isDarkMode = params.isDarkMode
        )
    }

    fun map(dtoParams: SettingsParamsDto): SettingsParams {
        return SettingsParams(
            remindTime = dtoParams.remindTime,
            isDarkMode = dtoParams.isDarkMode
        )
    }
}