package com.nemislimus.tratometr.authorization.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nemislimus.tratometr.authorization.domain.AuthInteractor
import com.nemislimus.tratometr.authorization.domain.TokensStorageInteractor
import com.nemislimus.tratometr.authorization.domain.models.Resource
import com.nemislimus.tratometr.authorization.domain.models.Tokens
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class RegistrationViewModel @Inject constructor(
    val authInteractor: AuthInteractor,
    val tokensStorageInteractor: TokensStorageInteractor
) : ViewModel() {

    private val _regState = MutableSharedFlow<Resource<Tokens>>(replay = 0)
    val regState = _regState.asSharedFlow()

    fun registration(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val resource = authInteractor.register(email, password)
            _regState.emit(resource)
        }
    }

    fun putTokensToStorage(tokens: Tokens) {
        tokensStorageInteractor.putTokens(tokens)
    }

    class Factory @Inject constructor(
        private val authInteractor: AuthInteractor,
        private val tokensStorageInteractor: TokensStorageInteractor
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == RegistrationViewModel::class.java)
            return RegistrationViewModel(authInteractor, tokensStorageInteractor) as T
        }
    }
}