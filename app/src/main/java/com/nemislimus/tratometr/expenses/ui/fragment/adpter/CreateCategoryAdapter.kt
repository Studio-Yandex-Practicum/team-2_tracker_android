package com.nemislimus.tratometr.expenses.ui.fragment.adpter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nemislimus.tratometr.databinding.ItemCreateCatIconBinding
import com.nemislimus.tratometr.expenses.ui.model.CreateCategoryItem
import com.nemislimus.tratometr.expenses.ui.fragment.viewholder.CreateCategoryViewHolder

class CreateCategoryAdapter(
    private val onIconItemClick: (resId: Int?) -> Unit,
) : RecyclerView.Adapter<CreateCategoryViewHolder>() {
    private val iconsItems: MutableList<CreateCategoryItem> = mutableListOf()

    fun setItems(items: List<CreateCategoryItem>) {
        iconsItems.clear()
        iconsItems.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreateCategoryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return CreateCategoryViewHolder(
            ItemCreateCatIconBinding.inflate(layoutInflater, parent, false)) { position ->

            if (position != RecyclerView.NO_POSITION) {
                val selectedIconRes = iconsItems.getOrNull(position)?.iconResId
                onIconItemClick(selectedIconRes)
                iconsItems.forEach { item ->
                    item.isSelected = item.iconResId == selectedIconRes
                }
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int = iconsItems.size

    override fun onBindViewHolder(holder: CreateCategoryViewHolder, position: Int) {
        iconsItems.getOrNull(position)?.let { iconItem ->
            holder.bind(iconItem)
        }
    }

}