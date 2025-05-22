package com.nemislimus.tratometr.expenses.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.a2t.myapplication.main.ui.activity.recycler.model.ScrollState
import com.google.android.material.datepicker.MaterialDatePicker
import com.nemislimus.tratometr.R
import com.nemislimus.tratometr.common.appComponent
import com.nemislimus.tratometr.common.util.BindingFragment
import com.nemislimus.tratometr.common.util.DateRangeHelper
import com.nemislimus.tratometr.common.util.ExpenseFilter
import com.nemislimus.tratometr.common.util.ExpenseFilterCallback
import com.nemislimus.tratometr.common.util.MoneyConverter
import com.nemislimus.tratometr.common.util.TimePreset
import com.nemislimus.tratometr.databinding.FragmentExpensesBinding
import com.nemislimus.tratometr.expenses.ui.fragment.adpter.ExpensesAdapter
import com.nemislimus.tratometr.expenses.ui.fragment.adpter.ExpensesAdapterListener
import com.nemislimus.tratometr.expenses.ui.fragment.recycler.ExpensesRecyclerView
import com.nemislimus.tratometr.expenses.ui.fragment.recycler.ExpensesScrollListener
import com.nemislimus.tratometr.expenses.ui.fragment.recycler.OnScrollStateChangedListener
import com.nemislimus.tratometr.expenses.ui.fragment.viewholder.ExpensesViewHolder
import com.nemislimus.tratometr.expenses.ui.viewmodel.ExpenseHistoryViewModel
import com.nemislimus.tratometr.expenses.ui.viewmodel.history_model.Historical
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class ExpensesFragment : BindingFragment<FragmentExpensesBinding>(), ExpenseFilterCallback, ExpensesAdapterListener,
    OnScrollStateChangedListener {

    @Inject
    lateinit var vmFactory: ExpenseHistoryViewModel.Factory
    private val viewModel: ExpenseHistoryViewModel by viewModels { vmFactory }

    private val adapter = ExpensesAdapter(this)
    private var mIth: ItemTouchHelper? = null
    private var recycler: RecyclerView? = null
    private var scrollState = ScrollState.STOPPED
    private var scrollJob = lifecycleScope.launch {}
    private var calendarJob: Job? = null
    private var isClickable = true

    companion object {
        const val SCROLL_POSITION = "scroll_position"
    }

    override fun onAttach(context: Context) {
        context.appComponent.inject(this)
        super.onAttach(context)
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentExpensesBinding {
        return FragmentExpensesBinding.inflate(inflater,container,false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler = binding.recycler
        initializingRecyclerView()  // Создание рециклера

        // Подписываемся на наблюдение за данными из БД
        viewModel.getExpensesLiveData().observe(viewLifecycleOwner) { state ->
            binding.progressBar.isVisible = false
            binding.tvSum.text = MoneyConverter.convertBigDecimalToRublesString(requireContext(),state.sum)
            val emptyList = state.expenses.isEmpty()
            binding.recycler.isVisible = !emptyList
            binding.placeholder.isVisible = emptyList
            updateRecyclerView(state.expenses)
            if (!emptyList && savedInstanceState != null) {
                    val scrollPosition = savedInstanceState.getInt(SCROLL_POSITION, 0)
                    recycler?.scrollToPosition(scrollPosition)
            }
        }

        // ПРОКРУТКА
        recycler?.addOnScrollListener(ExpensesScrollListener(this))

        binding.ivBtnScroll.setOnClickListener {
            when(scrollState) {
                ScrollState.DOWN -> recycler?.smoothScrollToPosition(adapter.itemCount - 1)
                ScrollState.UP -> recycler?.smoothScrollToPosition(0)
                else -> {}
            }
        }

        binding.ivSettings.setOnClickListener {
            requestFocusToCalendar()
            findNavController().navigate(
                R.id.action_expensesFragment_to_settingsFragment
            )
        }

        binding.ivDiagram.setOnClickListener {
            requestFocusToCalendar()
            findNavController().navigate(
                R.id.action_expensesFragment_to_analyticsFragment
            )
        }

        binding.btnAddExpense.setOnClickListener {
            requestFocusToCalendar()
            addExpense()
        }

        binding.ivCalendar.setOnClickListener {
            if (!isClickable) return@setOnClickListener
            isClickable = false

            requestFocusToCalendar()
            val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText(R.string.select_range)
                .build()
            dateRangePicker.addOnPositiveButtonClickListener { selection ->
                ExpenseFilter.setDateInterval(selection.first, selection.second + 24 * 60 * 60 * 1000, TimePreset.PERIOD)
            }
            dateRangePicker.show(requireActivity().supportFragmentManager, "DATE_RANGE_PICKER")

            calendarJob?.cancel()
            calendarJob = CoroutineScope(Dispatchers.Main).launch {
                delay(1000)
                isClickable = true
            }
        }
        binding.tvDay.setOnClickListener {
            requestFocusToCalendar()
            val range = DateRangeHelper.getCurrentDay()
            ExpenseFilter.setDateInterval(range.first, range.second, TimePreset.DAY)
        }
        binding.tvWeek.setOnClickListener {
            requestFocusToCalendar()
            val range = DateRangeHelper.getCurrentWeek()
            ExpenseFilter.setDateInterval(range.first, range.second, TimePreset.WEEK)
        }
        binding.tvMonth.setOnClickListener {
            requestFocusToCalendar()
            val range = DateRangeHelper.getCurrentMonth()
            ExpenseFilter.setDateInterval(range.first, range.second, TimePreset.MONTH)
        }
        binding.tvYear.setOnClickListener {
            requestFocusToCalendar()
            val range = DateRangeHelper.getCurrentYear()
            ExpenseFilter.setDateInterval(range.first, range.second, TimePreset.YEAR)
        }
        binding.tvCategories.setOnClickListener {
            requestFocusToCalendar()
            findNavController().navigate(
                R.id.action_expensesFragment_to_selectCategoryFragment
            )
        }

        recycler?.setOnTouchListener { _, _ ->
            requestFocusToCalendar()
            false
        }
    }

    override fun onStart() {
        super.onStart()
        // Подписываемся на наблюдение за фильтром
        ExpenseFilter.addExpenseFilterListener(this)
    }

    override fun onFilterChanged(expenseFilter: ExpenseFilter) {
        // Заполняем поле период
        binding.tvRange.text = DateRangeHelper.convertDatesInRange(
            expenseFilter.startDate,
            expenseFilter.endDate
        )
        // Выделяем нужную кнопку фильтра
        accentOnButton(expenseFilter.presetButton)

        // Выделяем Категорию, если выбрана
        val color = if(expenseFilter.categories != null) {
            ContextCompat.getColor(requireContext(), R.color.accented)
        } else {
            binding.tvSum.currentTextColor
        }
        binding.tvCategories.setTextColor(color)

        // Запрос на получение списка расходов
        binding.progressBar.isVisible = true
        viewModel.getExpenseListFilter(
            expenseFilter.startDate,
            expenseFilter.endDate,
            expenseFilter.categories
        )
    }

    private fun accentOnButton(presetButton: TimePreset?) {
        // Возвращаем все кнопки в исходное состояние
        changeBtnFilterBackground(binding.ivCalendar, false)
        changeBtnFilterBackground(binding.tvDay, false)
        changeBtnFilterBackground(binding.tvWeek, false)
        changeBtnFilterBackground(binding.tvMonth, false)
        changeBtnFilterBackground(binding.tvYear, false)
        // Выделяем нужную кнопку фильтра
        if (presetButton != null) {
            when (presetButton) {
                TimePreset.PERIOD -> changeBtnFilterBackground(binding.ivCalendar, true)
                TimePreset.DAY -> changeBtnFilterBackground(binding.tvDay, true)
                TimePreset.WEEK -> changeBtnFilterBackground(binding.tvWeek, true)
                TimePreset.MONTH -> changeBtnFilterBackground(binding.tvMonth, true)
                TimePreset.YEAR -> changeBtnFilterBackground(binding.tvYear, true)
            }
        }

    }

    private fun changeBtnFilterBackground (v: View, isAccent: Boolean) {
        val currentTextColor = binding.tvSum.currentTextColor
        if (v is TextView) {
            val res = if(isAccent) R.drawable.btn_blue_background_r6 else R.drawable.btn_white_background_r6
            val color = if(isAccent) ContextCompat.getColor(requireContext(), R.color.button_text) else currentTextColor
            v.setBackgroundResource(res)
            v.setTextColor(color)
        } else {
            val color = if (isAccent) {
                ContextCompat.getColor(requireContext(), R.color.accented)
            } else {
                currentTextColor
            }
            binding.ivCalendar.imageTintList = ColorStateList.valueOf(color)
        }
    }

    private fun initializingRecyclerView() {
        val recyclerView = ExpensesRecyclerView(this, recycler!!, adapter)
        recyclerView.setupRecyclerView()
        mIth = recyclerView.createItemTouchHelper()
        mIth!!.attachToRecyclerView(recycler)
    }

    private fun updateRecyclerView(list: List<Historical>) {
        adapter.items.clear()
        adapter.items.addAll(list)
        adapter.notifyDataSetChanged()
    }

    // Вызывается из адаптера для возврата Foreground в исходное положение
    override fun returnHolderToOriginalState(holder: ExpensesViewHolder) {
        ItemTouchHelper.Callback.getDefaultUIUtil().clearView(holder.flForeground)
    }

    override fun onDeleteExpense(expense: Historical.HistoryContent, position: Int) {
        requestFocusToCalendar()
        val id = expense.expense.id
        viewModel.deleteExpense(id)
        adapter.items.removeAt(position)
        adapter.notifyItemRemoved(position) // Уведомление об удалении
        adapter.notifyItemRangeChanged(position, adapter.items.size - 1)
    }

    override fun onEditExpense(expense: Historical.HistoryContent) {
        requestFocusToCalendar()
        findNavController().navigate(
            R.id.action_expensesFragment_to_createExpenseFragment,
            CreateExpenseFragment.createArgs(expense)
        )
    }

    private fun addExpense() {
        findNavController().navigate(R.id.action_expensesFragment_to_createExpenseFragment,
            CreateExpenseFragment.createArgs(null))
    }

    private fun requestFocusToCalendar(){
        with(binding.ivCalendar) {
            isFocusableInTouchMode = true
            requestFocus()
            isFocusableInTouchMode = false
        }
    }

    override fun onStop() {
        super.onStop()
        // Удаляем текущий фрагмент как наблюдателя фильтра
        ExpenseFilter.removeExpenseFilterListener(this)
    }

    // Отслеживание состояния прокрутки
    override fun onScrollStateChanged(scrollState: ScrollState) {
        when (scrollState) {
            ScrollState.DOWN -> {           // Прокрутка вниз
                this.scrollState = scrollState
                binding.ivBtnScroll.setImageResource(R.drawable.ic_scroll_down)
                binding.ivBtnScroll.isVisible = true
            }
            ScrollState.UP -> {             // Прокрутка вверх
                this.scrollState = scrollState
                binding.ivBtnScroll.setImageResource(R.drawable.ic_scroll_up)
                binding.ivBtnScroll.isVisible = true
            }
            ScrollState.STOPPED -> {        // Прокрутка остановлена
                this.scrollState = scrollState
            }
        }
        scrollJob.cancel()
        scrollJob = viewLifecycleOwner.lifecycleScope.launch {
            delay(1200)
            binding.ivBtnScroll.isVisible = false
        }
    }

    override fun updateTotalAmount(historicalList: List<Historical>) {
        val sum = historicalList
            .filterIsInstance<Historical.HistoryContent>() // Фильтруем только объекты HistoryContent
            .sumOf { it.expense.amount } // Суммируем значения amount
        binding.tvSum.text = MoneyConverter.convertBigDecimalToRublesString(requireContext(), sum)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Получаем текущую позицию прокрутки
        val layoutManager = recycler?.layoutManager as? LinearLayoutManager
        val scrollPosition = layoutManager?.findFirstVisibleItemPosition() ?: 0
        outState.putInt(SCROLL_POSITION, scrollPosition)
    }

    override fun onDestroyView() {
        scrollJob.cancel()
        super.onDestroyView()
    }
}