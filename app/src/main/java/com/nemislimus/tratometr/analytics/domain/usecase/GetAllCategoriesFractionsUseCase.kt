package com.nemislimus.tratometr.analytics.domain.usecase

import com.nemislimus.tratometr.analytics.domain.api.AnalyticsRepository
import com.nemislimus.tratometr.analytics.domain.model.CategoryFraction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

class GetAllCategoriesFractionsUseCase @Inject constructor(
    private val repository: AnalyticsRepository
) {

    suspend fun execute(startDate: Long?, endDate: Long?): List<CategoryFraction> {
        val categoryFractions: MutableList<CategoryFraction> = mutableListOf()
        var totalMoneySpend = BigDecimal("0")

        val allCategoriesList = withContext(Dispatchers.IO) {
            repository.getCategoriesListWithIconsFilter(startDate, endDate)
        }

        if (allCategoriesList.isNotEmpty()) {
            allCategoriesList.forEach { category ->
                val categoryAmount = withContext(Dispatchers.IO) {
                    repository.getExpenseAmountsListByCategoryFilter(startDate, endDate, category.name)
                }.sumOf { it }

                if (categoryAmount > BigDecimal.ZERO) {
                    totalMoneySpend += categoryAmount

                    val fraction = CategoryFraction(
                        name = category.name,
                        iconResId = category.iconResId,
                        fractionPercentValue = 0,
                        categorySumAmount = categoryAmount
                    )

                    categoryFractions.add(fraction)
                }
            }
        }

        if (categoryFractions.isNotEmpty()) {
            val fractions = categoryFractions.map { fraction ->
                fraction.copy(
                    fractionPercentValue = calculateCategoryPercent(fraction.categorySumAmount, totalMoneySpend)
                )
            }

            categoryFractions.clear()
            categoryFractions.addAll(fractions)
        }

        repository.closeDatabaseForAnalytics()
        return categoryFractions
    }

    private fun calculateCategoryPercent(amount: BigDecimal, total: BigDecimal): Int {
        return amount
            .multiply(BigDecimal(100))
            .divide(total, 0, RoundingMode.HALF_UP)
            .toInt()
    }

}