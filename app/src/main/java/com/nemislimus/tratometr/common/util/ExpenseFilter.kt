package com.nemislimus.tratometr.common.util

object ExpenseFilter {
    var startDate: Long? = null
        private set
    var endDate: Long? = null
        private set
    var presetButton: TimePreset? = null
        private set
    var categories: List<String>? = null
        set(value) {
            field = value
            notifyCallbacks() // Уведомляем слушателей об изменении
        }
    private val callbacks = mutableListOf<ExpenseFilterCallback>() // Список слушателей

    // Для введения периода (одновременного задания startDate и endDate)
    fun setDateInterval(start: Long?, end: Long?, preset: TimePreset?) {
        startDate = start
        endDate = end
        presetButton = preset
        notifyCallbacks()
    }

    fun resetFilterSettings () {
        startDate = null
        endDate = null
        presetButton = null
        categories = null
        notifyCallbacks()
    }

    // Метод для добавления наблюдателя
    fun addExpenseFilterListener(callback: ExpenseFilterCallback) {
        if (!callbacks.contains(callback)) {
            callbacks.add(callback) // Добавляем только если такого объекта еще нет в списке
            callback.onFilterChanged(this)
        }
    }

    // Метод для удаления наблюдателя
    fun removeExpenseFilterListener(callback: ExpenseFilterCallback) {
        callbacks.remove(callback)
    }

    // Метод для уведомления всех наблюдателей
    private fun notifyCallbacks() {
        for (callback in callbacks) {
            callback.onFilterChanged(this) // Передаем текущий объект ExpenseFilter
        }
    }
}