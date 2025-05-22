package com.nemislimus.tratometr.analytics.domain.model

import androidx.annotation.DrawableRes
import java.math.BigDecimal

data class CategoryFraction (
    val name: String,
    @DrawableRes val iconResId: Int,
    val fractionPercentValue: Int,
    val categorySumAmount: BigDecimal,
)