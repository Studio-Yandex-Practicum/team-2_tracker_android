package com.nemislimus.tratometr.common.util

import android.content.Context
import com.nemislimus.tratometr.R
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

object MoneyConverter {
    private val decimalFormatSymbols = DecimalFormatSymbols().apply {
        groupingSeparator = ' '
        decimalSeparator = ','
    }

    // Округляет BigDecimal до рубля и возвращает строку с разделителями разрядов и символом ₽ в конце
    fun convertBigDecimalToRublesString (context: Context, sum: BigDecimal): String {
        val roundedSum = sum.setScale(0, RoundingMode.HALF_UP)
        val decimalFormat = DecimalFormat("#,##0", decimalFormatSymbols)
        val formattedValue = decimalFormat.format(roundedSum)
        return String.format(context.getString(R.string.formatted_sum), formattedValue)
    }

    // Выводит C с копейками и возвращает строку с копейками с разделителями разрядов и символом ₽ в конце
    fun convertBigDecimalToRublesKopecksString (context: Context,sum: BigDecimal): String {
        val decimalFormat = DecimalFormat("#,##0.00", decimalFormatSymbols)
        val formattedValue = decimalFormat.format(sum)
        return String.format(context.getString(R.string.formatted_sum), formattedValue)
    }
}