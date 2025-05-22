package com.nemislimus.tratometr.expenses.ui.fragment.adpter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.nemislimus.tratometr.databinding.ItemCategoryListBinding
import com.nemislimus.tratometr.expenses.ui.model.SelectCategoryItem

class SelectCategoryAdapter(
    private val onCategoryClick: (categoryName: String, isSelected: Boolean) -> Unit,
) : RecyclerView.Adapter<SelectCategoryAdapter.SelectCategoryViewHolder>() {

    private val categoriesList: MutableList<SelectCategoryItem> = mutableListOf()
    private val filteredCategoryList: MutableList<SelectCategoryItem> = mutableListOf()
    private var selectedCategoryName: String = ""

    fun setCategories(items: List<SelectCategoryItem>) {
        categoriesList.clear()
        categoriesList.addAll(items)
        filterCategoriesByName()
    }

    fun filterCategoriesByName(name: String = "") {
        filteredCategoryList.clear()
        if(name.isBlank()) {
            filteredCategoryList.addAll(categoriesList)
        } else {
            val newList = categoriesList.filter { category -> category.name.startsWith(name, true) }
            filteredCategoryList.addAll(newList)
        }
        notifyDataSetChanged()
    }

    fun deselectAllCategories() {
        categoriesList.forEach { category ->
            category.isSelected = false
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = filteredCategoryList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectCategoryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return SelectCategoryViewHolder(
            ItemCategoryListBinding.inflate(layoutInflater, parent, false)) { position ->
            if (position != RecyclerView.NO_POSITION) {
                selectedCategoryName = filteredCategoryList[position].name
                categoriesList.forEach { item ->
                    if (item.name == selectedCategoryName) {
                        item.isSelected = !item.isSelected
                        onCategoryClick(selectedCategoryName, item.isSelected)
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: SelectCategoryViewHolder, position: Int) {
        filteredCategoryList.getOrNull(position)?.let { category ->
            holder.bind(category)
        }
    }

    inner class SelectCategoryViewHolder(
        private val binding: ItemCategoryListBinding,
        private val onCategoryItemClick: (position: Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(model: SelectCategoryItem) {
            val iconDrawable = ContextCompat.getDrawable(itemView.context, model.iconResId)
            binding.ivCategoryIcon.setImageDrawable(iconDrawable)
            binding.tvCategoryName.text = model.name
            binding.ivCategoryCheckbox.isVisible = model.isSelected

            itemView.setOnClickListener {
                onCategoryItemClick(bindingAdapterPosition)
                notifyDataSetChanged()
            }
        }
    }
}