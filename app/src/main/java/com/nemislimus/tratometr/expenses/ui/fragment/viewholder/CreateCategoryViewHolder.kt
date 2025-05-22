package com.nemislimus.tratometr.expenses.ui.fragment.viewholder

import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.nemislimus.tratometr.R
import com.nemislimus.tratometr.databinding.ItemCreateCatIconBinding
import com.nemislimus.tratometr.expenses.ui.model.CreateCategoryItem

class CreateCategoryViewHolder(
    private val binding: ItemCreateCatIconBinding,
    onProductClick: (position: Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            onProductClick(bindingAdapterPosition)
        }
    }

    private val selectedIconColor by lazy {
        ContextCompat.getColor(itemView.context, R.color.category_ic)
    }

    fun bind(model: CreateCategoryItem) {
        val iconDrawable = ContextCompat.getDrawable(itemView.context, model.iconResId)?.mutate()
        binding.ivCircle.isVisible = false
        binding.ivIcon.setImageDrawable(iconDrawable)

        if (model.isSelected) {
            binding.ivIcon.drawable.setTint(selectedIconColor)
            binding.ivCircle.isVisible = true
        }
    }
}