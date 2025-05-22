package com.nemislimus.tratometr.expenses.ui.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.nemislimus.tratometr.R
import com.nemislimus.tratometr.common.appComponent
import com.nemislimus.tratometr.common.util.BindingFragment
import com.nemislimus.tratometr.databinding.FragmentCreateCategoryBinding
import com.nemislimus.tratometr.expenses.ui.fragment.adpter.CreateCategoryAdapter
import com.nemislimus.tratometr.expenses.ui.viewmodel.CreateCategoryViewModel
import javax.inject.Inject

class CreateCategoryFragment : BindingFragment<FragmentCreateCategoryBinding>() {

    @Inject
    lateinit var vmFactory: CreateCategoryViewModel.Factory
    private val viewModel: CreateCategoryViewModel by viewModels { vmFactory }
    private val adapter = CreateCategoryAdapter {iconResId ->
        setSelectedIconRes(iconResId)
        hideKeyboardAndClearFocus()
    }

    private val fragmentsArgs by navArgs<CreateCategoryFragmentArgs>()

    @DrawableRes
    private var selectedIconRes: Int = R.drawable.ic_star
    private var textWatcher: TextWatcher? = null

    override fun onAttach(context: Context) {
        requireActivity().appComponent.inject(this)
        super.onAttach(context)
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCreateCategoryBinding {
        return FragmentCreateCategoryBinding.inflate(inflater,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvIconList.adapter = adapter
        adapter.setItems(viewModel.getIconsItems())
        installTextWatcher()

        binding.etTitleOfCategory.setText(fragmentsArgs.categoryName)

        binding.tbCreateCategory.setOnClickListener { findNavController().navigateUp() }

        binding.btnCategorySave.setOnClickListener {
            viewModel.saveCategory(
                binding.etTitleOfCategory.text.toString().trim(),
                selectedIconRes
            ) {
                setFragmentResult(
                    RESULT_KEY,
                    bundleOf(STRING_KEY to binding.etTitleOfCategory.text.toString())
                )
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyFragment() {
        binding.etTitleOfCategory.removeTextChangedListener(textWatcher)
        textWatcher = null
    }

    private fun installTextWatcher() {
        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.btnCategorySave.isEnabled = when(s) {
                    null -> false
                    else -> validateCategoryName(s.toString().trim())
                }
            }
        }

        binding.etTitleOfCategory.addTextChangedListener(textWatcher)
    }

    private fun validateCategoryName(name: String): Boolean {
        if (name.isEmpty()) {
            binding.etTitleOfCategoryLayout.error = ContextCompat
                .getString(requireContext(), R.string.required_field)
            return false
        } else if (viewModel.isCategoryExist(name)) {
            binding.etTitleOfCategoryLayout.error = ContextCompat
                .getString(requireContext(), R.string.category_already_exist)
            return false
        } else {
            binding.etTitleOfCategoryLayout.isErrorEnabled = false
        }
        return true
    }

    private fun setSelectedIconRes(@DrawableRes resId: Int?) {
        resId?.let { selectedIconRes = it }
    }

    private fun hideKeyboardAndClearFocus() {
        ContextCompat.getSystemService(requireContext(), InputMethodManager::class.java)
            ?.hideSoftInputFromWindow(binding.etTitleOfCategory.windowToken, 0)

        binding.etTitleOfCategory.clearFocus()
    }

    companion object {
        const val RESULT_KEY = "result_key"
        const val STRING_KEY = "string_key"
    }
}