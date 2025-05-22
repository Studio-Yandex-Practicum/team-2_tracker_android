package com.nemislimus.tratometr.authorization.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nemislimus.tratometr.authorization.domain.AuthInteractor
import com.nemislimus.tratometr.authorization.domain.models.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class PassRecoveryViewModel @Inject constructor(val authInteractor: AuthInteractor): ViewModel() {

    private val _recoveryResult = MutableSharedFlow<Resource<Boolean>>(replay = 0)
    var recoveryResult = _recoveryResult.asSharedFlow()

    fun recoverPass(email: String){
        viewModelScope.launch(Dispatchers.IO) {
            val result = authInteractor.recovery(email)
            _recoveryResult.emit(result)
        }
    }

    class Factory @Inject constructor(
        private val authInteractor: AuthInteractor,
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == PassRecoveryViewModel::class.java)
            return PassRecoveryViewModel(authInteractor) as T
        }
    }
}