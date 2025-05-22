package com.nemislimus.tratometr.expenses.ui.fragment.adpter

import com.nemislimus.tratometr.expenses.ui.fragment.viewholder.ExpensesViewHolder
import com.nemislimus.tratometr.expenses.ui.viewmodel.history_model.Historical

interface ExpensesAdapterListener {
    fun onDeleteExpense(expense: Historical.HistoryContent, position: Int)
    fun onEditExpense(expense: Historical.HistoryContent)
    fun returnHolderToOriginalState(holder: ExpensesViewHolder)
    fun updateTotalAmount(historicalList: List<Historical>)
}