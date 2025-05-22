package com.nemislimus.tratometr.expenses.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nemislimus.tratometr.expenses.domain.api.ExpenseHistoryInteractor
import com.nemislimus.tratometr.expenses.domain.model.Category
import com.nemislimus.tratometr.expenses.domain.model.Expense
import com.nemislimus.tratometr.expenses.ui.model.AutoCompleteItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CreateExpenseViewModel (
    private val interactor: ExpenseHistoryInteractor
): ViewModel() {


    fun getAllCategories(callback: (List<AutoCompleteItem>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val categories = getAllCategoriesListWithIcons()
            val items = convertCategoriesToAutoCompleteItems(categories)
            withContext(Dispatchers.Main) {
                callback(items)
            }
        }
    }

    private suspend fun getAllCategoriesListWithIcons() = withContext(Dispatchers.IO) {
        interactor.getAllCategoriesListWithIcons()
    }

    private fun convertCategoriesToAutoCompleteItems(categories: List<Category>): List<AutoCompleteItem> {
        return categories.map { category ->
            AutoCompleteItem(
                name = category.name,
                iconResId = category.iconResId,
                isAdd = false // Устанавливаем isAdd в false для всех элементов
            )
        }
    }

    fun addNewExpense(expense: Expense, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            interactor.addNewExpense(expense)
            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }

    fun updateExpense(expense: Expense, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            interactor.updateExpense(expense)
            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }

    class Factory @Inject constructor(
        private val interactor: ExpenseHistoryInteractor
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == CreateExpenseViewModel::class.java)
            return CreateExpenseViewModel(interactor) as T
        }
    }
}

