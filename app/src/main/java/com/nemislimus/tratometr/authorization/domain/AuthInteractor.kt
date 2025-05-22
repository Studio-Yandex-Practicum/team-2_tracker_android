package com.nemislimus.tratometr.authorization.domain

import com.nemislimus.tratometr.authorization.domain.models.Resource
import com.nemislimus.tratometr.authorization.domain.models.Tokens

interface AuthInteractor {
    suspend fun register(email: String, password: String): Resource<Tokens>
    suspend fun login(email: String, password: String): Resource<Tokens>
    suspend fun refresh(token: String): Resource<Tokens>
    suspend fun check(token: String?): Resource<Boolean>
    suspend fun recovery(email: String): Resource<Boolean>
}