package com.nemislimus.tratometr.expenses.ui.model


sealed interface CategoryListState {

    data class  Content(
        val categoriesItems: List<SelectCategoryItem>,
        val allCategoriesSelected: Boolean
    ): CategoryListState

    data object Empty: CategoryListState

}