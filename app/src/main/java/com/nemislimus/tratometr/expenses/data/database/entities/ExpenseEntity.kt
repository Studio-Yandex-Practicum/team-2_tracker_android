package com.nemislimus.tratometr.expenses.data.database.entities

data class ExpenseEntity(
    val id: Long,
    val date: Long,
    val amount: Long,
    val category: String,
    val iconResString: String,
    val description: String?
)