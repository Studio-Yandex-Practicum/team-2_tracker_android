package com.nemislimus.tratometr.authorization.domain.impl

import com.nemislimus.tratometr.authorization.domain.AuthInteractor
import com.nemislimus.tratometr.authorization.domain.AuthRepository
import com.nemislimus.tratometr.authorization.domain.models.Resource
import com.nemislimus.tratometr.authorization.domain.models.Tokens
import javax.inject.Inject

class AuthInteractorImpl @Inject constructor(private val repository: AuthRepository) : AuthInteractor {
    override suspend fun register(email: String, password: String): Resource<Tokens> {
        return repository.register(email, password)
    }

    override suspend fun login(email: String, password: String): Resource<Tokens> {
        return repository.login(email, password)
    }

    override suspend fun refresh(token: String): Resource<Tokens> {
        return repository.refresh(token)
    }

    override suspend fun check(token: String?): Resource<Boolean> {
        return repository.check(token)
    }

    override suspend fun recovery(email: String): Resource<Boolean> {
        return repository.recovery(email)
    }
}