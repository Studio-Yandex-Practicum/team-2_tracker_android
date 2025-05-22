package com.nemislimus.tratometr.expenses.ui.viewmodel.history_model

import java.math.BigDecimal

data class HistoryState(
    val sum: BigDecimal,
    val expenses: List<Historical>
)
