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

class AuthorizationViewModel @Inject constructor(
    private val authInteractor: AuthInteractor,
    private val tokensStorageInteractor: TokensStorageInteractor
) :
    ViewModel() {

    private val _authState = MutableSharedFlow<Resource<Tokens>>(replay = 0)
    val authState = _authState.asSharedFlow()

    fun authorize(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = authInteractor.login(email, password)
            _authState.emit(result)
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
            require(modelClass == AuthorizationViewModel::class.java)
            return AuthorizationViewModel(authInteractor, tokensStorageInteractor) as T
        }
    }
}