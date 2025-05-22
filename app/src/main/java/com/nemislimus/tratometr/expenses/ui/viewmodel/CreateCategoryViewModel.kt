package com.nemislimus.tratometr.expenses.ui.viewmodel

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nemislimus.tratometr.common.util.CustomCategoryIcons
import com.nemislimus.tratometr.expenses.domain.api.ExpenseHistoryInteractor
import com.nemislimus.tratometr.expenses.domain.model.Category
import com.nemislimus.tratometr.expenses.ui.model.CreateCategoryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CreateCategoryViewModel(
    private val interactor: ExpenseHistoryInteractor
) : ViewModel() {

    private var allCategories: Set<String> = emptySet()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getAllCategoriesNames()
        }
    }

    fun getIconsItems(): List<CreateCategoryItem> = CustomCategoryIcons.entries.map {
        CreateCategoryItem(
            iconResId = it.resId,
            isSelected = it == CustomCategoryIcons.STAR
        )
    }

    fun isCategoryExist(name: String): Boolean = allCategories.contains(name.lowercase())

    private suspend fun getAllCategoriesNames() {
        allCategories = interactor.getAllCategoriesList()
            .asSequence()
            .map { name -> name.lowercase() }
            .toSet()
    }

    fun saveCategory(name: String, @DrawableRes resId: Int, callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            interactor.addNewCategory(Category(name, resId))
            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }

    class Factory @Inject constructor(
        private val expenseInteractor: ExpenseHistoryInteractor
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == CreateCategoryViewModel::class.java)
            return CreateCategoryViewModel(expenseInteractor) as T
        }
    }

}