package com.nemislimus.tratometr.expenses.ui.viewmodel.history_model

import com.nemislimus.tratometr.expenses.domain.model.Expense

sealed interface Historical {
     data class HistoryDate(val date: String): Historical
     data class HistoryContent(val expense: Expense): Historical
}