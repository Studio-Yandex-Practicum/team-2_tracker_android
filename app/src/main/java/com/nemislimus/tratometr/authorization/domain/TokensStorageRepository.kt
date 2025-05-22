package com.nemislimus.tratometr.authorization.domain

import com.nemislimus.tratometr.authorization.domain.models.Tokens

interface TokensStorageRepository {
    fun putTokens(tokens: Tokens)
    fun getTokens(): Tokens
    fun clearTokens()
}