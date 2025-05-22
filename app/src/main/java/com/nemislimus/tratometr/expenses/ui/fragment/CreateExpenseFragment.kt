package com.nemislimus.tratometr.expenses.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.nemislimus.tratometr.R
import com.nemislimus.tratometr.common.appComponent
import com.nemislimus.tratometr.common.util.BindingFragment
import com.nemislimus.tratometr.databinding.FragmentCreateExpenseBinding
import com.nemislimus.tratometr.expenses.domain.model.Expense
import com.nemislimus.tratometr.expenses.ui.model.AutoCompleteItem
import com.nemislimus.tratometr.expenses.ui.fragment.adpter.AutoCompleteAdapter
import com.nemislimus.tratometr.expenses.ui.viewmodel.CreateExpenseViewModel
import com.nemislimus.tratometr.expenses.ui.viewmodel.history_model.Historical
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Calendar
import java.util.TimeZone
import javax.inject.Inject

class CreateExpenseFragment : BindingFragment<FragmentCreateExpenseBinding>() {

    companion object {
        private const val EXTRA_EXPENSE = "EXTRA_EXPENSE"

        fun createArgs(expense: Historical.HistoryContent? ): Bundle =
            bundleOf(EXTRA_EXPENSE to expense?.expense)
    }

    private var expense: Expense? = null
    private var iconResId = 0
    private var isAddMode = true
    private lateinit var itemsOriginal: List<AutoCompleteItem>
    private lateinit var items: MutableList<AutoCompleteItem>
    private lateinit var adapter: AutoCompleteAdapter
    private lateinit var typedValue: TypedValue
    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private var currentText: String = ""
    private var scale = 0f
    private lateinit var ivIcon: ImageView
    private var dateInMilisecond = 0L

    @Inject
    lateinit var vmFactory: CreateExpenseViewModel.Factory
    private val viewModel: CreateExpenseViewModel by viewModels { vmFactory }

    override fun onAttach(context: Context) {
        context.appComponent.inject(this)
        super.onAttach(context)
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCreateExpenseBinding {
        return FragmentCreateExpenseBinding.inflate(inflater,container,false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        autoCompleteTextView = binding.actvCategory
        ivIcon = binding.ivIcon

        // Восстановление состояния
        if (savedInstanceState != null) {
            autoCompleteTextView.setText(savedInstanceState.getString("Caregory"))
            binding.etAmount.setText(savedInstanceState.getString("Amount"))
            binding.etDescription.setText(savedInstanceState.getString("Description"))
            dateInMilisecond = savedInstanceState.getLong("Date", System.currentTimeMillis())
            binding.tvDate.text = DateFormat.format("dd.MM.yyyy", dateInMilisecond).toString()
        } else {
            expense = getExpense()  // Получаем расход
        }

        scale = requireContext().resources.displayMetrics.density   // Получаем плотность экрана
        typedValue = TypedValue()
        items = mutableListOf()
        adapter = AutoCompleteAdapter(requireContext(), items)
        autoCompleteTextView.setAdapter(adapter)
        updateItems()                       // Получаем список категорий
        isAddMode = expense == null
        if (isAddMode) showAddMode() else showEditMode()

        binding.tvTitle.setOnClickListener { findNavController().popBackStack() }

        binding.root.setOnClickListener {
            requestTouchFocus(binding.root)
            hideKeyboard()
        }

        binding.ivClear.setOnTouchListener { _, _ ->
            autoCompleteTextView.setText("")
            false
        }

        autoCompleteTextView.threshold = 1  // Начинать показывать предложения после ввода 1 символа
        // Открыть список при клике или получении фокуса
        autoCompleteTextView.setOnClickListener { autoCompleteTextView.showDropDown() }
        autoCompleteTextView.setOnFocusChangeListener { _, hasFocus ->
            val isCategory = itemsOriginal.any { it.name == autoCompleteTextView.text.toString() }
            if (hasFocus) {
                binding.ivClear.isVisible = true
                autoCompleteTextView.showDropDown()
            } else {
                binding.ivClear.isVisible = isCategory
            }
            isSeparatorVisible(!hasFocus)
        }

        autoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = (parent.getItemAtPosition(position) as AutoCompleteItem)
            if (!selectedItem.isAdd) {
                autoCompleteTextView.setText(selectedItem.name)
            } else {
                autoCompleteTextView.setText(currentText)
                openCategoryWindow(autoCompleteTextView.text.toString())
            }
        }
        autoCompleteTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                currentText = s.toString()
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val currentStr = s.toString()
                // Редактируем список адаптера
                val isCategory = itemsOriginal.any { it.name == currentStr }
                if (items.size > 0 && isCategory) autoCompleteTextView.dismissDropDown() // Скрываем список
            }
            override fun afterTextChanged(s: Editable?) {
                val currentText = s.toString()
                // Проверка, надо ли сделать кнопку Сохранить активной
                binding.btnAction.isEnabled = enableBtnAction()
                val item = itemsOriginal.find { it.name == currentText } // Если есть такая категория,
                showIcons(item?.iconResId)  // вставляем ее иконку, нет - удаляем
                item?.let { errorState(false, autoCompleteTextView) }    // Убираем индикацию ошибки
            }
        })

        binding.tvDate.setOnClickListener {
            requestTouchFocus(binding.tvDate)
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(R.string.select_date_expense)
                .build()
            datePicker.addOnPositiveButtonClickListener {
                dateInMilisecond = datePicker.selection!!
                binding.tvDate.text = DateFormat.format("dd.MM.yyyy", dateInMilisecond).toString()
            }
            datePicker.show(requireActivity().supportFragmentManager, "date_picker")
        }

        binding.etAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    val parts = s.toString().split(".")
                    if (parts.size == 2 && parts[1].length > 2) {                       // Если введен третий симовол после точки
                        binding.etAmount.setText(s.substring(0, s.length - 1))          // Отменяем ввод последнего символа
                        binding.etAmount.setSelection(binding.etAmount.text.length)     // Устанавливаем курсор в конец текста
                    }
                }
            }
            override fun afterTextChanged(s: Editable?) {
                // Проверка, надо ли сделать кнопку Сохранить активной
                binding.btnAction.isEnabled = enableBtnAction()
                // Если сумма введена правильно, убираем индикацию ошибки
                if (binding.etAmount.text.isNotEmpty()) {
                    val amount = BigDecimal(binding.etAmount.text.toString()).setScale(2, RoundingMode.HALF_UP)
                    if (amount >= BigDecimal("0.01") && amount <= BigDecimal("999999999999999")) {
                        errorState(false, binding.etAmount)
                    }
                }
            }
        })
        binding.etAmount.setOnFocusChangeListener { _, hasFocus -> isSeparatorVisible(!hasFocus) }

        binding.etDescription.setOnFocusChangeListener { _, hasFocus -> isSeparatorVisible(!hasFocus) }

        binding.btnAction.setOnClickListener {
            hideKeyboard()
            if (!errorInCaregoryOrAmount()) executeSave()
        }
    }

    private fun executeSave() {
        val newExpense = Expense(
            expense?.id ?: 0,
            dateInMilisecond,
            BigDecimal(binding.etAmount.text.toString()),
            autoCompleteTextView.text.toString(),
            iconResId,
            binding.etDescription.text.toString()
        )
        if (isAddMode) {
            viewModel.addNewExpense(newExpense) {
                findNavController().navigateUp()
            }
        } else {
            viewModel.updateExpense(newExpense) {
                findNavController().navigateUp()
            }
        }
    }

    private fun openCategoryWindow(category: String){

        setFragmentResultListener(CreateCategoryFragment.RESULT_KEY) {_, bundle ->
            val categoryName = bundle.getString(CreateCategoryFragment.STRING_KEY) ?: ""
            autoCompleteTextView.setText(categoryName)
        }
        val direction = CreateExpenseFragmentDirections.actionCreateExpenseFragmentToCreateCategoryFragment(category)
        findNavController().navigate(direction)
    }

    private fun showIcons(iconResId: Int?) {
        val leftPadding = if (iconResId != null) 64 else 16
        autoCompleteTextView.setPadding(
            (leftPadding * scale + 0.5f).toInt(),
            (12 * scale + 0.5f).toInt(),
            (48 * scale + 0.5f).toInt(),
            (12 * scale + 0.5f).toInt()
        )
        ivIcon.isVisible = iconResId != null
        if (iconResId != null) {
            ivIcon.setImageDrawable(AppCompatResources.getDrawable(requireContext(), iconResId))
            binding.ivClear.setImageResource(R.drawable.ic_edit)
            binding.ivClear.isVisible = true
            this.iconResId = iconResId
        } else {
            binding.ivClear.setImageResource(R.drawable.ic_close)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showEditMode() {
        dateInMilisecond = expense!!.date
        binding.tvTitle.setText(R.string.edit_expense)
        binding.btnAction.setText(R.string.btn_save)
        showIcons(expense!!.iconResId)
        autoCompleteTextView.setText(expense!!.category)
        binding.tvDate.text = DateFormat.format("dd.MM.yyyy", dateInMilisecond).toString()
        binding.etAmount.setText(expense!!.amount.toString())
        expense!!.description?.let { binding.etDescription.setText(expense!!.description) }
        // Проверка, надо ли сделать кнопку Сохранить активной
        binding.btnAction.isEnabled = enableBtnAction()
    }

    private fun showAddMode() {
        dateInMilisecond = today()
        binding.tvTitle.setText(R.string.add_expense)
        binding.btnAction.setText(R.string.btn_add)
        binding.tvDate.text = DateFormat.format("dd.MM.yyyy", dateInMilisecond).toString()
    }

    private fun errorInCaregoryOrAmount(): Boolean {
        val isErrorCategory = errorInCaregory()
        val isErrorAmount = errorInAmount()
        return (isErrorCategory || isErrorAmount)
    }

    private fun errorInCaregory(): Boolean {
        if (itemsOriginal.any { it.name == autoCompleteTextView.text.toString() }) {
            errorState(false, autoCompleteTextView)
            return false
        } else {
            errorState(true, autoCompleteTextView)
            return true
        }
    }

    private fun errorInAmount(): Boolean {
        val amount = BigDecimal(binding.etAmount.text.toString()).setScale(2, RoundingMode.HALF_UP)
        if (amount >= BigDecimal("0.01") && amount <= BigDecimal("999999999999999")) {
            errorState(false, binding.etAmount)
            return false
        } else {
            errorState(true, binding.etAmount)
            return true
        }
    }

    private fun enableBtnAction(): Boolean {
        return (autoCompleteTextView.text.isNotEmpty() && binding.etAmount.text.isNotEmpty())
    }

    private fun errorState(isError: Boolean, view: EditText) {
        if (isError) {
            requireContext().theme.resolveAttribute(R.attr.appErrorColor, typedValue, true)
            view.setTextColor(typedValue.data)
            view.setBackgroundResource(R.drawable.card_background_error)
        } else {
            requireContext().theme.resolveAttribute(R.attr.appTextPrimary, typedValue, true)
            view.setTextColor(typedValue.data)
            view.setBackgroundResource(R.drawable.card_background_selector)
        }
        if (view == binding.actvCategory) {
            binding.categoryError.isVisible = isError
        } else {
            binding.amountError.isVisible = isError
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("Caregory", autoCompleteTextView.text.toString())
        outState.putLong("Date", dateInMilisecond)
        outState.putString("Amount", binding.etAmount.text.toString())
        outState.putString("Description", binding.etDescription.text.toString())
    }

    private fun updateItems() {
        viewModel.getAllCategories { newItems ->
            itemsOriginal = newItems                // Сохраняем отдельно исходный список
            // Подготовка списка для адаптера
            val sortedItems = newItems.sortedBy { it.name }.toMutableList() // Сортировка списка по алфавиту
            sortedItems.add(0, AutoCompleteItem("", 0, true))
            val item = itemsOriginal.find { it.name == autoCompleteTextView.text.toString() }
            item?.let { showIcons(it.iconResId) }
            // Смена списка в адаптере
            items.clear()
            items.addAll(sortedItems)
            adapter.notifyDataSetChanged()
        }
    }

    private fun today(): Long {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun getExpense(): Expense? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getSerializable(EXTRA_EXPENSE, Expense::class.java)
        } else {
            requireArguments().getSerializable(EXTRA_EXPENSE) as? Expense
        }
    }

    private fun requestTouchFocus(view: View) {
        with(view) {
            isFocusableInTouchMode = true
            requestFocus()
            isFocusableInTouchMode = false
        }
    }

    private fun isSeparatorVisible (isVisible: Boolean) {
        binding.separator.isVisible = isVisible
    }

    private fun hideKeyboard () {
        if (view != null) {
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view?.windowToken, 0)
            isSeparatorVisible(true)
        }
    }
}