package com.nemislimus.tratometr.authorization.domain.impl

import com.nemislimus.tratometr.authorization.domain.TokensStorageInteractor
import com.nemislimus.tratometr.authorization.domain.TokensStorageRepository
import com.nemislimus.tratometr.authorization.domain.models.Tokens
import javax.inject.Inject

class TokensStorageInteractorImpl @Inject constructor(private val repository: TokensStorageRepository) :
    TokensStorageInteractor {
    override fun putTokens(tokens: Tokens) {
        repository.putTokens(tokens)
    }

    override fun getTokens(): Tokens {
        return repository.getTokens()
    }

    override fun clearTokens() {
        repository.clearTokens()
    }
}