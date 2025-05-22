package com.nemislimus.tratometr.expenses.ui.fragment.adpter

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nemislimus.tratometr.R
import com.nemislimus.tratometr.databinding.ItemHistoryDateBinding
import com.nemislimus.tratometr.databinding.ItemHistoryExpenseBinding
import com.nemislimus.tratometr.expenses.ui.fragment.ExpensesFragment
import com.nemislimus.tratometr.expenses.ui.fragment.viewholder.ExpensesDateViewHolder
import com.nemislimus.tratometr.expenses.ui.fragment.viewholder.ExpensesViewHolder
import com.nemislimus.tratometr.expenses.ui.viewmodel.history_model.Historical

class ExpensesAdapter(private val listener: ExpensesAdapterListener): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items = mutableListOf<Historical>()

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is Historical.HistoryDate -> 1
            is Historical.HistoryContent -> 2
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        return when (viewType) {
            1 -> ExpensesDateViewHolder(ItemHistoryDateBinding.inflate(layoutInspector, parent, false))
            2 -> ExpensesViewHolder(ItemHistoryExpenseBinding.inflate(layoutInspector, parent, false))
            else -> error("Не известный viewType = [$viewType]. Не могу создать холдер")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            1 -> {
                val dateHolder = holder as ExpensesDateViewHolder
                dateHolder.bind(items[position] as Historical.HistoryDate)
            }
            2 -> {
                val expenseHolder = holder as ExpensesViewHolder
                val item = items[position] as Historical.HistoryContent
                expenseHolder.bind(item)
                addListeners(expenseHolder, item, position)
            }
            else -> error("Не известный viewType = [$holder.itemViewType]. Не могу создать холдер")
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun addListeners(holder: ExpensesViewHolder, item: Historical.HistoryContent, position: Int) {
        holder.flBackground.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                listener.returnHolderToOriginalState(holder)
            }
        }

        holder.flForeground.setOnClickListener{
            holder.icon.requestFocus()
        }

        holder.icon.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                makeTextViewMultiline(holder.tvCategory)
                makeTextViewMultiline(holder.tvDescription)
            } else {
                makeTextViewSingleLine(holder.tvCategory)
                makeTextViewSingleLine(holder.tvDescription)
            }
        }

        holder.btnDel.setOnClickListener {
            with(holder.btnDel) {
                isFocusableInTouchMode = true
                requestFocus()
                isFocusableInTouchMode = true
            }
            holder.flForeground.startAnimation(
                AnimationUtils.loadAnimation((listener as ExpensesFragment).requireContext(), R.anim.del_holder)
            ) // Анимация удаления
            listener.onDeleteExpense(item, position)
            listener.updateTotalAmount(items)
        }

        holder.btnEdit.setOnClickListener {
            listener.onEditExpense(item)
        }
    }

    private fun makeTextViewMultiline(tv: TextView) {
        with(tv) {
            ellipsize = null
            isSingleLine = false
        }
    }

    private fun makeTextViewSingleLine(tv: TextView) {
        with(tv) {
            ellipsize = TextUtils.TruncateAt.END
            isSingleLine = true
        }
    }

    override fun getItemCount(): Int = items.size
}