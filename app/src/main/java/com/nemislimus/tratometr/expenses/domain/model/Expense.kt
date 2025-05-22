package com.nemislimus.tratometr.expenses.domain.model

import androidx.annotation.DrawableRes
import java.io.Serializable
import java.math.BigDecimal

data class Expense(
    val id: Long,
    val date: Long,
    val amount: BigDecimal,
    val category: String,
    @DrawableRes val iconResId: Int,
    val description: String?
) : Serializable