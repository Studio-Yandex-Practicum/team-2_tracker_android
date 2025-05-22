package com.nemislimus.tratometr.expenses.data.database

import com.nemislimus.tratometr.expenses.data.database.entities.CategoryEntity
import com.nemislimus.tratometr.expenses.data.database.entities.ExpenseEntity
import com.nemislimus.tratometr.expenses.domain.api.ExpenseHistoryRepository
import com.nemislimus.tratometr.expenses.domain.model.Category
import com.nemislimus.tratometr.expenses.domain.model.Expense
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class ExpenseHistoryRepositoryImpl @Inject constructor(
    private val expenseHistoryDao: ExpenseHistoryDao,
    private val dbConverter: DBConverter
): ExpenseHistoryRepository {

    private val mutex = Mutex()

    // ################   ЗАПРОСЫ ДЛЯ ОКНА ИСТОРИЯ РАСХОДОВ   #########################################################################################
    // Выборка строк-расходов за период и по категории
    override fun getExpenseListFilter(startDate: Long?, endDate: Long?, categories: List<String>?): Flow<List<Expense>> = flow {
        mutex.withLock {
            val expenses = expenseHistoryDao.getExpenseListFilter(startDate, endDate, categories)
            emit(convertFromExpenseEntity(expenses))
        }
    }

    private fun convertFromExpenseEntity(expenses: List<ExpenseEntity>): List<Expense> {
        return expenses.map { expense -> dbConverter.map(expense) }
    }
    // Удаление строки-расхода по id
    override fun deleteExpense(expenseId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            mutex.withLock {
                expenseHistoryDao.deleteExpense(expenseId)
            }
        }
    }

    // ################   ЗАПРОСЫ ДЛЯ ОКНА ДОБАВЛЕНИЕ/РЕДАКТИРОВАНИЕ РАСХОДА   ######################################################################
    //Список всех категорий с иконками (по алфавиту)
    override fun getAllCategoriesListWithIcons(): List<Category> {
        val categories = expenseHistoryDao.getAllCategoriesListWithIcons()
        return convertFromCategoriesEntity(categories)
    }

    private fun convertFromCategoriesEntity(categories: List<CategoryEntity>): List<Category> {
        return categories.map { category -> dbConverter.map(category) }
    }
    // Добавление нового расхода
    override fun addNewExpense(expense: Expense) {
        expenseHistoryDao.addNewExpense(dbConverter.map(expense))
    }
    // Обновление расхода
    override fun updateExpense(expense: Expense) {
        expenseHistoryDao.updateExpense(dbConverter.map(expense))
    }

    // ################   ЗАПРОСЫ ДЛЯ ОКНА ВЫБОР КАТЕГОРИИ   ########################################################################################
    // Список категорий с иконками и фильтром(период)
    override fun getCategoriesListWithIconsRange(startDate: Long?, endDate: Long?): List<Category> {
        val categories = expenseHistoryDao.getCategoriesListWithIconsRange(startDate, endDate)
        return convertFromCategoriesEntity(categories)
    }

    // ################   ЗАПРОСЫ ДЛЯ ОКНА СОЗДАНИЕ КАТЕГОРИИ   #####################################################################################
    // Список всех категорий
    override suspend fun getAllCategoriesList(): List<String> {
        return expenseHistoryDao.getAllCategoriesList()
    }
    // Добавление новой категорий
    override suspend fun addNewCategory(category: Category) {
        expenseHistoryDao.addNewCategory(dbConverter.map(category))
    }
}