package com.nemislimus.tratometr.expenses.ui.fragment.adpter

import android.content.Context
import android.widget.Filter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.nemislimus.tratometr.R
import com.nemislimus.tratometr.expenses.ui.model.AutoCompleteItem

class AutoCompleteAdapter(
    context: Context,
    private val originalItems: List<AutoCompleteItem>
) : ArrayAdapter<AutoCompleteItem>(context, 0, originalItems) {

    private var filteredItems: List<AutoCompleteItem> = originalItems

    override fun getCount(): Int {
        return filteredItems.size
    }

    override fun getItem(position: Int): AutoCompleteItem {
        return filteredItems[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = getItem(position)

        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_autocomplete, parent, false)

        val iconImageView: ImageView = view.findViewById(R.id.icon)
        val textView: TextView = view.findViewById(R.id.text)
        val textAdd: TextView = view.findViewById(R.id.text_add)

        textView.text = item.name
        textView.isVisible = item.name.isNotEmpty()
        if (item.isAdd) {
            textAdd.isVisible = true
            iconImageView.setBackgroundResource(R.drawable.btn_plus_background)
            iconImageView.setImageResource(R.drawable.ic_plus)
        } else {
            textAdd.isVisible = false
            iconImageView.setBackgroundResource(R.drawable.circle_icons)
            iconImageView.setImageResource(item.iconResId)
        }

        return view
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                val filterPattern = constraint?.toString()?.lowercase()?.trim() ?: ""
                if (originalItems[0].isAdd) originalItems[0].name = constraint.toString()
                results.values = if (filterPattern.isEmpty()) {
                    originalItems
                } else {
                    originalItems.filter {
                        it.name.lowercase().contains(filterPattern)
                    }
                }
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredItems = results?.values as? List<AutoCompleteItem> ?: originalItems
                notifyDataSetChanged()
            }
        }
    }
}