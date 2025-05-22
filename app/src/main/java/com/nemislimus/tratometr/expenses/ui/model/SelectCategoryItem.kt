package com.nemislimus.tratometr.expenses.ui.model

import androidx.annotation.DrawableRes

data class SelectCategoryItem(
    val name: String,
    @DrawableRes val iconResId: Int,
    var isSelected: Boolean
)