package com.nemislimus.tratometr.expenses.domain.impl

import com.nemislimus.tratometr.expenses.domain.api.ExpenseHistoryInteractor
import com.nemislimus.tratometr.expenses.domain.api.ExpenseHistoryRepository
import com.nemislimus.tratometr.expenses.domain.model.Category
import com.nemislimus.tratometr.expenses.domain.model.Expense
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExpenseHistoryInteractorImpl @Inject constructor(
    private val repository: ExpenseHistoryRepository
): ExpenseHistoryInteractor {

    // ################   ЗАПРОСЫ ДЛЯ ОКНА ИСТОРИЯ РАСХОДОВ   #########################################################################################
    // Выборка строк-расходов за период и по категории
    override fun getExpenseListFilter(startDate: Long?, endDate: Long?, categories: List<String>?): Flow<List<Expense>> {
        return repository.getExpenseListFilter(startDate, endDate, categories)
    }

    // Удаление строки-расхода по id
    override fun deleteExpense(expenseId: Long) {
        repository.deleteExpense(expenseId)
    }

    // ################   ЗАПРОСЫ ДЛЯ ОКНА ДОБАВЛЕНИЕ/РЕДАКТИРОВАНИЕ РАСХОДА   ######################################################################
    //Список всех категорий с иконками (по алфавиту)
    override fun getAllCategoriesListWithIcons(): List<Category> {
       return repository.getAllCategoriesListWithIcons()
    }

    // Добавление нового расхода
    override fun addNewExpense(expense: Expense) {
        repository.addNewExpense(expense)
    }
    // Обновление расхода
    override fun updateExpense(expense: Expense) {
        repository.updateExpense(expense)
    }

    // ################   ЗАПРОСЫ ДЛЯ ОКНА ВЫБОР КАТЕГОРИИ   ########################################################################################
    // Список категорий с иконками и фильтром(период)
    override fun getCategoriesListWithIconsRange(startDate: Long?, endDate: Long?): List<Category> {
        return repository.getCategoriesListWithIconsRange(startDate, endDate)
    }

    // ################   ЗАПРОСЫ ДЛЯ ОКНА СОЗДАНИЕ КАТЕГОРИИ   #####################################################################################
    // Список всех категорий
    override suspend fun getAllCategoriesList(): List<String> {
        return repository.getAllCategoriesList()
    }
    // Добавление новой категорий
    override suspend fun addNewCategory(category: Category) {
        repository.addNewCategory(category)
    }
}