package com.nemislimus.tratometr.expenses.ui.fragment.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.nemislimus.tratometr.R
import com.nemislimus.tratometr.databinding.ItemHistoryDateBinding
import com.nemislimus.tratometr.expenses.ui.viewmodel.history_model.Historical
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExpensesDateViewHolder(private val binding: ItemHistoryDateBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Historical.HistoryDate) {
        val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
        val currentDate = Date()
        val currentYear = SimpleDateFormat("yyyy", Locale.getDefault()).format(currentDate)
        val today = dateFormat.format(currentDate)
        if (item.date == today) {
            binding.tvDate.setText(R.string.today)
        } else {
            binding.tvDate.text = getDateString(item.date, currentYear)
        }
    }

    private fun getDateString(date: String, currentYear: String): String {
        return date.split(" ").let { parts ->
            if (parts[2] == currentYear) "${parts[0]} ${parts[1]}" else "${parts[0]} ${parts[1]} ${parts[2]}"
        }
    }
}