package com.nemislimus.tratometr.expenses.domain.model

import androidx.annotation.DrawableRes

data class Category(
    val name: String,
    @DrawableRes val iconResId: Int
)
