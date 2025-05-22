package com.nemislimus.tratometr.expenses.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nemislimus.tratometr.expenses.domain.api.ExpenseHistoryInteractor
import com.nemislimus.tratometr.expenses.domain.model.Expense
import com.nemislimus.tratometr.expenses.ui.viewmodel.history_model.ExpenseWithStringDate
import com.nemislimus.tratometr.expenses.ui.viewmodel.history_model.Historical
import com.nemislimus.tratometr.expenses.ui.viewmodel.history_model.HistoryState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

class ExpenseHistoryViewModel (
    private val interactor: ExpenseHistoryInteractor
): ViewModel() {

    private var expensesLiveData = MutableLiveData(HistoryState(BigDecimal(0),listOf()))

    fun getExpensesLiveData(): LiveData<HistoryState> = expensesLiveData

    fun getExpenseListFilter(startDate: Long?, endDate: Long?, categories: List<String>?) {
        viewModelScope.launch(Dispatchers.IO) {
            interactor
                .getExpenseListFilter(startDate, endDate, categories)
                .collect {
                    withContext(Dispatchers.Default) {
                        val sum = it.sumOf { exp -> exp.amount }
                        val list = processingResults(it)
                        expensesLiveData.postValue(HistoryState(sum, list))
                    }
                }
        }
    }

    private fun convertExpenseToExpenseWithStringDate(expense: Expense): ExpenseWithStringDate {
        val currentLocale = Locale.getDefault()
        val dateFormat = SimpleDateFormat("d MMMM yyyy", currentLocale)
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return ExpenseWithStringDate(
            expense.id,
            dateFormat.format(expense.date),
            expense.amount,
            expense.category,
            expense.iconResId,
            expense.description
        )
    }

    private fun processingResults(expenses: List<Expense>): List<Historical> {
        val expensesWithStringDate = expenses.map { convertExpenseToExpenseWithStringDate(it) }
        val datesList = expensesWithStringDate // Получаем список дат(строк)
            .map { it.date }
            .distinct()
        val historyList = mutableListOf<Historical>()
        for (date in datesList) {
            historyList.add(Historical.HistoryDate(date))
            for (exp in expensesWithStringDate) {
                if (exp.date == date) {
                    val expense = expenses.find { it.id == exp.id }
                    historyList.add(Historical.HistoryContent(expense!!))
                }
            }
        }
        return historyList
    }

    fun deleteExpense(expenseId: Long) {
        interactor.deleteExpense(expenseId)
    }

    class Factory @Inject constructor(
        private val interactor: ExpenseHistoryInteractor
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == ExpenseHistoryViewModel::class.java)
            return ExpenseHistoryViewModel(interactor) as T
        }
    }

}