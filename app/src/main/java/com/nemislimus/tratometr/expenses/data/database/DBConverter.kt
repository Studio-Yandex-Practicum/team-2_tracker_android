package com.nemislimus.tratometr.expenses.data.database

import android.content.Context
import com.nemislimus.tratometr.expenses.data.database.entities.CategoryEntity
import com.nemislimus.tratometr.expenses.data.database.entities.ExpenseEntity
import com.nemislimus.tratometr.expenses.domain.model.Category
import com.nemislimus.tratometr.expenses.domain.model.Expense
import java.math.BigDecimal
import java.math.RoundingMode

class DBConverter(
    val context: Context
) {
    fun map(expense: Expense): ExpenseEntity {
        return ExpenseEntity (
            expense.id,
            expense.date,
            map(expense.amount),
            expense.category,
            map(expense.iconResId),
            expense.description
        )
    }

    fun map(expense: ExpenseEntity): Expense {
        return Expense(
            expense.id,
            expense.date,
            map(expense.amount),
            expense.category,
            map(expense.iconResString),
            expense.description
        )
    }

    fun map(category: Category): CategoryEntity {
        return CategoryEntity (
            category.name,
            map(category.iconResId)
        )
    }

    fun map(category: CategoryEntity): Category {
        return Category (
            category.name,
            map(category.iconResString)
        )
    }

    fun map(amount: Long): BigDecimal {
        return BigDecimal(amount).divide(BigDecimal(100), 2, RoundingMode.HALF_UP)
    }

    private fun map(amount: BigDecimal): Long {
        return amount.multiply(BigDecimal("100")).toLong()
    }

    private fun map(iconResString: String): Int {
        return context.resources.getIdentifier(iconResString, "drawable", context.packageName)
    }

    private fun map(iconResId: Int): String {
        return context.resources.getResourceEntryName(iconResId)
    }
}