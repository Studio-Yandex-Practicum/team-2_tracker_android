package com.nemislimus.tratometr.expenses.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nemislimus.tratometr.common.util.ExpenseFilter
import com.nemislimus.tratometr.expenses.domain.api.ExpenseHistoryInteractor
import com.nemislimus.tratometr.expenses.domain.model.Category
import com.nemislimus.tratometr.expenses.ui.model.CategoryListState
import com.nemislimus.tratometr.expenses.ui.model.SelectCategoryItem
import javax.inject.Inject

class SelectCategoryViewModel(
    private val interactor: ExpenseHistoryInteractor
) : ViewModel() {

    var selectedCategoriesList: List<String> = listOf()

    init {
        ExpenseFilter.categories?.let { selectedCategoriesList = it } ?: listOf<String>()
    }

    private val state = MutableLiveData<CategoryListState>()
    fun observeState(): LiveData<CategoryListState> = state

    fun getAllCategories() {
        val categories = interactor.getAllCategoriesListWithIcons()
        when {
            categories.isEmpty() -> state.postValue(CategoryListState.Empty)
            else -> state.postValue(
                CategoryListState.Content(
                    mapCatsToItems(categories),
                    checkAllCategoriesMode(),
                )
            )
        }
    }

    private fun mapCatsToItems(categories: List<Category>): List<SelectCategoryItem> {
        return categories.map { cat ->
            SelectCategoryItem(
                name = cat.name,
                iconResId = cat.iconResId,
                setCategorySelectedStatus(cat.name)
            )
        }
    }

    private fun checkAllCategoriesMode(): Boolean {
        return selectedCategoriesList.isEmpty()
    }

    private fun setCategorySelectedStatus(name: String): Boolean {
        return selectedCategoriesList.contains(name)
    }


    class Factory @Inject constructor(
        private val expenseInteractor: ExpenseHistoryInteractor
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == SelectCategoryViewModel::class.java)
            return SelectCategoryViewModel(expenseInteractor) as T
        }
    }

}