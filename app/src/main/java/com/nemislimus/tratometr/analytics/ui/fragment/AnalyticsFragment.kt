package com.nemislimus.tratometr.analytics.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.fragment.app.viewModels
import com.nemislimus.tratometr.analytics.ui.viewmodel.AnalyticsViewModel
import com.nemislimus.tratometr.common.appComponent
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.nemislimus.tratometr.R
import com.nemislimus.tratometr.analytics.domain.model.CategoryFraction
import com.nemislimus.tratometr.analytics.ui.fragment.adapter.AnalyticsAdapter
import com.nemislimus.tratometr.analytics.ui.model.AnalyticsState
import com.nemislimus.tratometr.common.util.BindingFragment
import com.nemislimus.tratometr.common.util.DateRangeHelper
import com.nemislimus.tratometr.common.util.ExpenseFilter
import com.nemislimus.tratometr.common.util.ExpenseFilterCallback
import com.nemislimus.tratometr.common.util.MoneyConverter
import com.nemislimus.tratometr.common.util.TimePreset
import com.nemislimus.tratometr.databinding.FragmentAnalyticsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class AnalyticsFragment : BindingFragment<FragmentAnalyticsBinding>(), ExpenseFilterCallback {

    @Inject
    lateinit var vmFactory: AnalyticsViewModel.Factory
    private val viewModel: AnalyticsViewModel by viewModels { vmFactory }

    private val adapter = AnalyticsAdapter { clickOnOtherCategoryFraction() }
    private val datePresetViews = arrayOfNulls<View>(5)

    private lateinit var animationFadeIn: Animation


    override fun onAttach(context: Context) {
        requireActivity().appComponent.inject(this)
        animationFadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        super.onAttach(context)
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAnalyticsBinding {
        return FragmentAnalyticsBinding.inflate(inflater,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUiConfigurations()
    }

    override fun onStart() {
        super.onStart()

        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            stateProcessing(state)
        }

        ExpenseFilter.addExpenseFilterListener(this)
    }

    override fun onStop() {
        ExpenseFilter.removeExpenseFilterListener(this)
        super.onStop()
    }

    override fun onFilterChanged(expenseFilter: ExpenseFilter) {
        binding.tvDateRange.text = DateRangeHelper.convertDatesInRange(
            expenseFilter.startDate,
            expenseFilter.endDate
        )

        setDatePresetChipsColors(datePresetViews, expenseFilter.presetButton?.index)

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
            viewModel.getFractionsByFilter(
                expenseFilter.startDate,
                expenseFilter.endDate
            )
        }

        binding.tvTotalAmount.startAnimation(animationFadeIn)
        binding.tvDateRange.startAnimation(animationFadeIn)
        binding.rcvMainChart.startAnimation(animationFadeIn)
    }

    private fun stateProcessing(state: AnalyticsState) {
        when(state) {
            is AnalyticsState.Content -> showContent(state.fractions, state.byDescent)
            AnalyticsState.Empty -> showPlaceholder()
        }
    }

    private fun showContent(fractions: List<CategoryFraction>, sortByDesc: Boolean) {
        adapter.setFractions(fractions, sortByDesc)

        if (fractions.size > AnalyticsViewModel.NUMB_OF_COLORS) {
            if (sortByDesc) {
                binding.rvAnalyticsList.smoothScrollToPosition(fractions.size)
            } else {
                binding.rvAnalyticsList.smoothScrollToPosition(0)
            }
        }

        setSortIcon(sortByDesc)
        setRingChartData(fractions, sortByDesc)

        if (!binding.grAnalyticsContent.isVisible) {
            binding.pbAnalyticsProgressBar.isVisible = false
            binding.grPlaceholderAnalytics.isVisible = false
            binding.grAnalyticsContent.isVisible = true
        }
    }

    private fun showPlaceholder() {
        setRingChartData()
        binding.grAnalyticsContent.isVisible = false
        binding.pbAnalyticsProgressBar.isVisible = false
        binding.grPlaceholderAnalytics.isVisible = true
    }

    private fun setSortIcon(byDesc: Boolean) {
        if (byDesc) {
            binding.tvSortCategories
                .setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_sort_down, 0, 0, 0)
        } else {
            binding.tvSortCategories
                .setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_sort_up, 0, 0, 0)
        }
    }

    private fun setRingChartData(
        fractions: List<CategoryFraction> = listOf(),
        byDesc: Boolean = false
    ) {
        val chartList = viewModel.getFractionsForChart(fractions, byDesc)
        val totalAmount = MoneyConverter.convertBigDecimalToRublesString(
            requireContext(),
            chartList.sumOf { it }
        )

        binding.tvTotalAmount.text = totalAmount
        binding.rcvMainChart.setData(chartList)
    }

    private fun clickOnSortButton() {
        viewModel.sortingFractions()
    }

    private fun clickOnOtherCategoryFraction() {
        viewModel.getFractionsWithOthers()
    }

    private fun setDatePresetChipsColors (presetViews: Array<View?>, presetIndexFromFilter: Int?) {
        presetIndexFromFilter?.let {
            val defaultTextColor = getThemeAttrColor(R.attr.appTextPrimary)
            val selectedIconColor = getThemeAttrColor(R.attr.appAccentColor)
            val selectedTextColor = ContextCompat.getColor(requireContext(), R.color.button_text)

            presetViews.forEachIndexed { index, view ->
                view?.let {
                    if (view is TextView) {
                        if (index == presetIndexFromFilter) {
                            view.setTextColor(selectedTextColor)
                            view.setBackgroundResource(R.drawable.btn_blue_background_r6)
                        } else {
                            view.setTextColor(defaultTextColor)
                            view.setBackgroundResource(R.drawable.btn_white_background_r6)
                        }
                    } else if(view is ImageView){
                        if (index == presetIndexFromFilter) {
                            view.setColorFilter(selectedIconColor)
                        } else {
                            view.clearColorFilter()
                        }
                    }
                }
            }
        }
    }

    private fun getThemeAttrColor(@AttrRes resId: Int): Int {
        val themeResourceValue = TypedValue()
        val theme = requireContext().theme
        theme.resolveAttribute(resId, themeResourceValue, true)
        return themeResourceValue.data
    }

    private fun setUiConfigurations() {
        binding.rvAnalyticsList.adapter = adapter

        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_analyticsFragment_to_settingsFragment)
        }

        binding.btnHistory.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.tvSortCategories.setOnClickListener {
            clickOnSortButton()
        }

        setDatePresetViewsArray()
        setDatePresetListeners()
    }

    private fun setDatePresetViewsArray() {
        with(binding) {
            datePresetViews[0] = ivCalendarPreset
            datePresetViews[1] = tvDayPreset
            datePresetViews[2] = tvWeekPreset
            datePresetViews[3] = tvMonthPreset
            datePresetViews[4] = tvYearPreset
        }
    }

    private fun setDatePresetListeners() {
        binding.ivCalendarPreset.setOnClickListener {
            val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText(R.string.select_range)
                .build()

            dateRangePicker.addOnPositiveButtonClickListener { selection ->
                ExpenseFilter.setDateInterval(
                    selection.first,
                    selection.second + DAY_GAP,
                    TimePreset.PERIOD
                )
            }

            dateRangePicker.show(parentFragmentManager, DATE_RANGE_PICKER)
        }

        binding.tvDayPreset.setOnClickListener {
            val dayInterval = DateRangeHelper.getCurrentDay()
            ExpenseFilter.setDateInterval(
                dayInterval.first,
                dayInterval.second,
                TimePreset.DAY
            )
        }

        binding.tvWeekPreset.setOnClickListener {
            val weekInterval = DateRangeHelper.getCurrentWeek()
            ExpenseFilter.setDateInterval(
                weekInterval.first,
                weekInterval.second,
                TimePreset.WEEK
            )
        }

        binding.tvMonthPreset.setOnClickListener {
            val monthInterval = DateRangeHelper.getCurrentMonth()
            ExpenseFilter.setDateInterval(
                monthInterval.first,
                monthInterval.second,
                TimePreset.MONTH
            )
        }

        binding.tvYearPreset.setOnClickListener {
            val yearInterval = DateRangeHelper.getCurrentYear()
            ExpenseFilter.setDateInterval(
                yearInterval.first,
                yearInterval.second,
                TimePreset.YEAR
            )
        }
    }

    companion object {
        const val DATE_RANGE_PICKER = "date_range_picker"
        const val DAY_GAP: Long = 24 * 60 * 60 * 1000
    }

}