package com.nemislimus.tratometr.expenses.domain.api

import com.nemislimus.tratometr.expenses.domain.model.Category
import com.nemislimus.tratometr.expenses.domain.model.Expense
import kotlinx.coroutines.flow.Flow

interface ExpenseHistoryRepository {
    // ################   ЗАПРОСЫ ДЛЯ ОКНА ИСТОРИЯ РАСХОДОВ   #########################################################################################
    // Выборка строк-расходов за период и по категории
    fun getExpenseListFilter(startDate: Long?, endDate: Long?, categories: List<String>?): Flow<List<Expense>>
    // Удаление строки-расхода по id
    fun deleteExpense(expenseId: Long)

    // ################   ЗАПРОСЫ ДЛЯ ОКНА ДОБАВЛЕНИЕ/РЕДАКТИРОВАНИЕ РАСХОДА   ######################################################################
    //Список всех категорий с иконками (по алфавиту)
    fun getAllCategoriesListWithIcons(): List<Category>
    // Добавление нового расхода
    fun addNewExpense(expense: Expense)
    // Обновление расхода
    fun updateExpense(expense: Expense)

    // ################   ЗАПРОСЫ ДЛЯ ОКНА ВЫБОР КАТЕГОРИИ   ########################################################################################
    // Список категорий с иконками и фильтром(период)
    fun getCategoriesListWithIconsRange(startDate: Long?, endDate: Long?): List<Category>

    // ################   ЗАПРОСЫ ДЛЯ ОКНА СОЗДАНИЕ КАТЕГОРИИ   #####################################################################################
    // Список всех категорий
    suspend fun getAllCategoriesList(): List<String>
    // Добавление новой категорий
    suspend fun addNewCategory(category: Category)
}