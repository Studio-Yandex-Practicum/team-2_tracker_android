package com.nemislimus.tratometr.common.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

// Выдает startDate и endDate для кнопок День, Неделя, Месяц, Год
// ВНИМАНИЕ! ВСЕ МЕТОДЫ СТАТИЧЕСКИЕ
object DateRangeHelper {
    // Возвращает строку период типа "01.03 - 25.04.25" или "Период", если startDate и endDate == null
    fun convertDatesInRange(startDate: Long?, endDate: Long?): String {
        if (startDate == null || endDate == null) return "" // Возвращаем пустую строку
        val currentLocale = Locale.getDefault()
        val dateFormat = SimpleDateFormat("dd.MM.yy", currentLocale)
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val startDateStr = dateFormat.format(startDate)
        val endDateStr = dateFormat.format(endDate-1L)
        // Если даты совпадают, возвращаем только одну дату
        if (startDateStr == endDateStr) return endDateStr
        // Разбиваем даты на компоненты
        val startDateList = startDateStr.split(".")
        val endDateList = endDateStr.split(".")
        return when {
            // Если годы разные, выводим полный диапазон
            startDateList[2] != endDateList[2] -> {
                "$startDateStr - $endDateStr"
            }
            // Если месяцы разные, выводим день.месяц начала - конечную дату
            startDateList[1] != endDateList[1] -> {
                "${startDateList[0]}.${startDateList[1]} - ${endDateList[0]}.${endDateList[1]}.${endDateList[2]}"
            }
            // Если разные только дни, выводим день начала - конечную дату
            else -> {
                "${startDateList[0]} - ${endDateList[0]}.${endDateList[1]}.${endDateList[2]}"
            }
        }
    }

    // Возвращает временной диапазон <0:00 текущей даты, 0:00 следующей даты
    fun getCurrentDay(): Pair<Long, Long> {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val start = calendar.timeInMillis
        val end = start + 24 * 60 * 60 * 1000
        return Pair(start, end)
    }

    // Возвращает временной диапазон <0:00 понедельника текущей недели, 0:00 понедельника следующей недели
    fun getCurrentWeek(): Pair<Long, Long> {
        // Получение текущей даты
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        // Установка первого дня недели (например, понедельник)
        calendar.firstDayOfWeek = Calendar.MONDAY
        // Получение начала недели
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val start = calendar.timeInMillis
        // Получение конца недели
        val end = start + 7 * 24 * 60 * 60 * 1000
        return Pair(start, end)
    }

    // Возвращает временной диапазон <0:00 первого дня текущего месяца, 0:00 первого дня следующего месяца
    fun getCurrentMonth(): Pair<Long, Long> {
        // Получение текущей даты
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        // Установка первого дня месяца
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val start = calendar.timeInMillis
        // Установка последнего дня месяца
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        val end = calendar.timeInMillis + 24 * 60 * 60 * 1000
        return Pair(start, end)
    }

    // Возвращает временной диапазон <0:00 первого дня текущего года, 0:00 первого дня следующего года
    fun getCurrentYear(): Pair<Long, Long> {
        // Получение текущей даты
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        // Устанавливаем начало года
        calendar.set(Calendar.MONTH, Calendar.JANUARY)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val start = calendar.timeInMillis
        // Устанавливаем конец года
        calendar.set(Calendar.MONTH, Calendar.DECEMBER)
        calendar.set(Calendar.DAY_OF_MONTH, 31)
        calendar.set(Calendar.HOUR_OF_DAY, 24)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val end = calendar.timeInMillis
        return Pair(start, end)
    }
}