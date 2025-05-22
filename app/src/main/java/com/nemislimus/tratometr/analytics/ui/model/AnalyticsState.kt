package com.nemislimus.tratometr.analytics.ui.model

import com.nemislimus.tratometr.analytics.domain.model.CategoryFraction

sealed interface AnalyticsState {

    data object Empty : AnalyticsState

    data class Content(
        val fractions: List<CategoryFraction>,
        val byDescent: Boolean
    ) : AnalyticsState
}