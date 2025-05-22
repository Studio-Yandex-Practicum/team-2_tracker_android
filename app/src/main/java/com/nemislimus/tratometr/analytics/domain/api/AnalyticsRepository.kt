package com.nemislimus.tratometr.analytics.domain.api

import com.nemislimus.tratometr.expenses.domain.model.Category
import java.math.BigDecimal

interface AnalyticsRepository {
    // Список категорий с иконками и фильтром(период, категория)
    fun getCategoriesListWithIconsFilter(startDate: Long?, endDate: Long?): List<Category>

    // Список сумм расходов по категории с фильтром (период, категория)
    fun getExpenseAmountsListByCategoryFilter(startDate: Long?, endDate: Long?, category: String?): List<BigDecimal>

    fun closeDatabaseForAnalytics()
}