package com.nemislimus.tratometr.expenses.ui.model

import androidx.annotation.DrawableRes

data class AutoCompleteItem(
    var name: String,
    @DrawableRes val iconResId: Int,
    val isAdd: Boolean
)