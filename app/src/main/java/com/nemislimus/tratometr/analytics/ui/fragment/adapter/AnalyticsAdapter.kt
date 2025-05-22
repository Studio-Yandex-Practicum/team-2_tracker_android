package com.nemislimus.tratometr.analytics.ui.fragment.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.nemislimus.tratometr.R
import com.nemislimus.tratometr.analytics.domain.model.CategoryFraction
import com.nemislimus.tratometr.common.util.MoneyConverter
import com.nemislimus.tratometr.common.util.RingChartColorsResources
import com.nemislimus.tratometr.databinding.ItemAnalyticsCategoryListBinding
import com.nemislimus.tratometr.databinding.ItemAnalyticsOtherListBinding

class AnalyticsAdapter(
    private val changeListOpenStatus: () -> Unit,
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val fractions: ArrayList<CategoryFraction> = arrayListOf()
    private var sortByDescending = true

    fun setFractions(items: List<CategoryFraction>, byDesc: Boolean) {
        fractions.clear()
        fractions.addAll(items)
        sortByDescending = byDesc
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            OTHER_FRACTION -> AnalyticsOtherViewHolder(
                ItemAnalyticsOtherListBinding.inflate(layoutInflater, parent, false)
            )

            FRACTION -> AnalyticsViewHolder(
                ItemAnalyticsCategoryListBinding.inflate(layoutInflater, parent, false)
            )

            else -> throw ClassNotFoundException(R.string.unknown_viewholder_create.toString())
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AnalyticsOtherViewHolder -> {
                fractions.getOrNull(position)?.let { holder.bind(it) }
            }

            is AnalyticsViewHolder -> {
                fractions.getOrNull(position)?.let { holder.bind(it) }
            }

            else -> throw ClassNotFoundException(R.string.unknown_viewholder_bind.toString())
        }
    }

    override fun getItemCount(): Int = fractions.size

    override fun getItemViewType(position: Int): Int {
        return if (sortByDescending) {
            if (position <= NUMB_OF_COLORS - 1) FRACTION else OTHER_FRACTION
        } else {
            if (fractions.size <= NUMB_OF_COLORS) {
                FRACTION
            } else {
                if (position < fractions.size - NUMB_OF_COLORS) OTHER_FRACTION else FRACTION
            }
        }
    }


    inner class AnalyticsViewHolder(
        private val binding: ItemAnalyticsCategoryListBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(model: CategoryFraction) {
            val elementPosition = bindingAdapterPosition
            val iconDrawable: Drawable?
            val percentText ="${model.fractionPercentValue}%"
            val money: String = MoneyConverter.convertBigDecimalToRublesString(itemView.context, model.categorySumAmount)
            val name: String
            val iconBackColor: Int

            val otherCategoryFractionPosition = if (sortByDescending) {
                NUMB_OF_COLORS - 1
            } else {
                fractions.size - NUMB_OF_COLORS
            }

            val colorIndex = if (sortByDescending) {
                elementPosition
            } else {
                fractions.size - elementPosition - 1
            }

            if (elementPosition == otherCategoryFractionPosition) {
                iconDrawable = ContextCompat.getDrawable(itemView.context, R.drawable.ic_main_cat_other)
                name = ContextCompat.getString(itemView.context, R.string.category_other)
                iconBackColor = ContextCompat.getColor(itemView.context, RingChartColorsResources.COLOR_OTHERS.resId)

                itemView.setOnClickListener { changeListOpenStatus.invoke() }

            } else {
                iconDrawable = ContextCompat.getDrawable(itemView.context, model.iconResId)
                name = model.name
                iconBackColor = ContextCompat.getColor(itemView.context, RingChartColorsResources.entries[colorIndex].resId)
            }

            binding.ivCategoryIconAnalytic.setImageDrawable(iconDrawable)
            binding.ivCategoryIconBackAnalytic.setColorFilter(iconBackColor)
            binding.tvCategoryNameAnalytic.text = name
            binding.tvPercentAnalytic.text = percentText
            binding.tvAmountAnalytics.text = money
        }
    }

    inner class AnalyticsOtherViewHolder(
        private val binding: ItemAnalyticsOtherListBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(model: CategoryFraction) {
            binding.ivOtherIconAnalytic.setImageDrawable(
                ContextCompat.getDrawable(itemView.context, model.iconResId)
            )

            binding.ivOtherIconBackAnalytic.setColorFilter(
                ContextCompat.getColor(itemView.context, RingChartColorsResources.COLOR_OTHERS.resId)
            )

            binding.tvOtherNameAnalytic.text = model.name

            binding.tvOtherAmountAnalytics.text = MoneyConverter
                .convertBigDecimalToRublesString(itemView.context, model.categorySumAmount)
        }
    }

    companion object {
        const val NUMB_OF_COLORS = 8
        private const val FRACTION = 0
        private const val OTHER_FRACTION = 1
    }
}