package com.nemislimus.tratometr.analytics.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nemislimus.tratometr.analytics.domain.model.CategoryFraction
import com.nemislimus.tratometr.analytics.domain.usecase.GetAllCategoriesFractionsUseCase
import com.nemislimus.tratometr.analytics.ui.model.AnalyticsState
import java.math.BigDecimal
import javax.inject.Inject

class AnalyticsViewModel(
    private val getFractionsUseCase: GetAllCategoriesFractionsUseCase
) : ViewModel() {

    private var sortedByDesc = true
    private var withOthersCategories = false
    private var originRequestedFractions: List<CategoryFraction> = listOf()

    private val state = MutableLiveData<AnalyticsState>()
    fun observeState(): LiveData<AnalyticsState> = state

    suspend fun getFractionsByFilter(startDate: Long?, endDate: Long?) {
        val fractionsFromDatabase = getFractionsUseCase.execute(startDate, endDate)
        if (fractionsFromDatabase.isNotEmpty()) {
            originRequestedFractions = fractionsFromDatabase
            setState(AnalyticsState.Content(generateFractionList(), sortedByDesc))
        } else {
            setState(AnalyticsState.Empty)
        }
    }

    fun sortingFractions() {
        sortedByDesc = !sortedByDesc
        setState(AnalyticsState.Content(generateFractionList(), sortedByDesc))
    }

    fun getFractionsWithOthers() {
        withOthersCategories = !withOthersCategories
        setState(AnalyticsState.Content(generateFractionList(), sortedByDesc))
    }

    fun getFractionsForChart(list: List<CategoryFraction>, byDesc: Boolean): List<BigDecimal> {
        return if (byDesc) {
            list.take(NUMB_OF_COLORS).map { fraction -> fraction.categorySumAmount }
        } else {
            list.takeLast(NUMB_OF_COLORS).reversed().map { fraction -> fraction.categorySumAmount }
        }
    }

    private fun generateFractionList(
        byDesc: Boolean = sortedByDesc,
        withOthers: Boolean = withOthersCategories
    ): List<CategoryFraction> {

        val generatedFractionList: List<CategoryFraction>

        if (byDesc) {
            val sortedList = originRequestedFractions.sortedByDescending { it.categorySumAmount }
            val coloredList: List<CategoryFraction> = sortedList.take(NUMB_OF_COLORS - 1)
            val othersFractions = sortedList.drop(NUMB_OF_COLORS - 1)
            val otherFractionInstance = createOtherFractionInstance(othersFractions)

            generatedFractionList = if (withOthers) {
                if (othersFractions.isNotEmpty()) {
                    coloredList + otherFractionInstance + othersFractions
                } else {
                    coloredList
                }
            } else {
                if (othersFractions.isNotEmpty()) {
                    coloredList + otherFractionInstance
                } else {
                    coloredList
                }
            }

        } else {
            val sortedList = originRequestedFractions.sortedBy { it.categorySumAmount }
            val coloredList: List<CategoryFraction> = sortedList.takeLast(NUMB_OF_COLORS - 1)
            val othersFractions = sortedList.dropLast(NUMB_OF_COLORS - 1)
            val otherFractionInstance = createOtherFractionInstance(othersFractions)

            generatedFractionList = if (withOthers) {
                if (othersFractions.isNotEmpty()) {
                    othersFractions + otherFractionInstance + coloredList
                } else {
                    coloredList
                }
            } else {
                if (othersFractions.isNotEmpty()) {
                    listOf(otherFractionInstance) + coloredList
                } else {
                    coloredList
                }
            }
        }

        return generatedFractionList
    }

    private fun createOtherFractionInstance(otherFractions: List<CategoryFraction>): CategoryFraction {
        return CategoryFraction(
            "",
            0,
            otherFractions.sumOf { it.fractionPercentValue },
            otherFractions.sumOf { it.categorySumAmount }
        )
    }

    private fun setState(newState: AnalyticsState) {
        state.postValue(newState)
    }

    class Factory @Inject constructor(
        private val useCase: GetAllCategoriesFractionsUseCase
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == AnalyticsViewModel::class.java)
            return AnalyticsViewModel(useCase) as T
        }
    }

    companion object {
        const val NUMB_OF_COLORS = 8
    }
}