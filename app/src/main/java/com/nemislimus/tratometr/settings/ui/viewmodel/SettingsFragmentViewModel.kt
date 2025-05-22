package com.nemislimus.tratometr.settings.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nemislimus.tratometr.authorization.domain.TokensStorageInteractor
import com.nemislimus.tratometr.common.util.AppNotificationManager
import com.nemislimus.tratometr.expenses.domain.api.ExpenseHistoryInteractor
import com.nemislimus.tratometr.expenses.domain.model.Expense
import com.nemislimus.tratometr.settings.domain.api.SettingsRepository
import com.nemislimus.tratometr.settings.domain.model.SettingsParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsFragmentViewModel(
    private val repository: SettingsRepository,
    private val tokensStorageInteractor: TokensStorageInteractor,
    private val expenseInteractor: ExpenseHistoryInteractor,
) : ViewModel() {

    private val settingsParams = MutableLiveData<SettingsParams>()
    fun observeSettingsParams(): LiveData<SettingsParams> = settingsParams

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getSettingsParams()
        }
    }

    suspend fun saveSettings(params: SettingsParams) {
        repository.saveSettings(params)
    }

    suspend fun getSettingsParams() {
        settingsParams.postValue(repository.getSettings())
    }

    fun logOut() {
        tokensStorageInteractor.clearTokens()
    }

    fun getExpensesFlow(): Flow<List<Expense>> {
        return expenseInteractor.getExpenseListFilter(null, null, null)
    }

    fun correctTimeString(hours: Int, minutes: Int): String {
        return when {
            hours < 10 && minutes < 10 -> "0$hours:0$minutes"
            hours >= 10 && minutes < 10 -> "$hours:0$minutes"
            hours < 10 && minutes >= 10 -> "0$hours:$minutes"
            else -> "$hours:$minutes"
        }
    }

    fun setNotification(context: Context, hour: Int, minute: Int) =
        AppNotificationManager.setNotification(context, hour, minute)

    fun cancelNotification(context: Context) =
        AppNotificationManager.cancelNotification(context)


    class Factory @Inject constructor(
        private val repository: SettingsRepository,
        private val tokensStorageInteractor: TokensStorageInteractor,
        private val expenseInteractor: ExpenseHistoryInteractor
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == SettingsFragmentViewModel::class.java)
            return SettingsFragmentViewModel(
                repository,
                tokensStorageInteractor,
                expenseInteractor,
            ) as T
        }
    }

}