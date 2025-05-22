package com.nemislimus.tratometr.analytics.data.db.dao

import com.nemislimus.tratometr.analytics.domain.api.AnalyticsRepository
import com.nemislimus.tratometr.expenses.data.database.DBConverter
import com.nemislimus.tratometr.expenses.data.database.entities.CategoryEntity
import com.nemislimus.tratometr.expenses.domain.model.Category
import java.math.BigDecimal
import javax.inject.Inject

class AnalyticsRepositoryImpl @Inject constructor(
    private val analyticsDao: AnalyticsDao,
    private val dbConverter: DBConverter
): AnalyticsRepository {
    // Список категорий с иконками и фильтром (период, категория - для аналитики ставим null)
    override fun getCategoriesListWithIconsFilter(startDate: Long?, endDate: Long?): List<Category> {
        val expenses = analyticsDao.getCategoriesListWithIconsFilter(startDate, endDate)
        return(convertFromExpenseEntity(expenses))
    }

    private fun convertFromExpenseEntity(categories: List<CategoryEntity>): List<Category> {
        return categories.map { category -> dbConverter.map(category) }
    }

    // Список сумм расходов по категории с фильтром (период, категория)
    override fun getExpenseAmountsListByCategoryFilter(startDate: Long?, endDate: Long?, category: String?): List<BigDecimal> {
        val amounts = analyticsDao.getExpenseAmountsListByCategoryFilter(startDate, endDate, category)
        return (convertFromLongToBigDecimal(amounts))
    }

    private fun convertFromLongToBigDecimal(amounts: List<Long>): List<BigDecimal> {
        return amounts.map { amount -> dbConverter.map(amount) }
    }

    override fun closeDatabaseForAnalytics() {
        analyticsDao.closeDatabase()
    }
}