package com.nemislimus.tratometr.expenses.ui.viewmodel.history_model

import androidx.annotation.DrawableRes
import java.math.BigDecimal

data class ExpenseWithStringDate(
    val id: Long,
    val date: String,
    val amount: BigDecimal,
    val category: String,
    @DrawableRes val iconResId: Int,
    val description: String?
)
