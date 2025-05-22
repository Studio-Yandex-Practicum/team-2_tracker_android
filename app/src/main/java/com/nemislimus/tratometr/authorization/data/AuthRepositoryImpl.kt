package com.nemislimus.tratometr.authorization.data

import com.nemislimus.tratometr.authorization.data.dto.AuthRequest
import com.nemislimus.tratometr.authorization.data.dto.AuthResponse
import com.nemislimus.tratometr.authorization.data.dto.CheckTokenRequest
import com.nemislimus.tratometr.authorization.data.dto.CheckTokenResponse
import com.nemislimus.tratometr.authorization.data.dto.RecoveryRequest
import com.nemislimus.tratometr.authorization.data.dto.RefreshTokenRequest
import com.nemislimus.tratometr.authorization.data.dto.RefreshTokenResponse
import com.nemislimus.tratometr.authorization.data.network.NetworkClient
import com.nemislimus.tratometr.authorization.domain.AuthRepository
import com.nemislimus.tratometr.authorization.domain.models.Resource
import com.nemislimus.tratometr.authorization.domain.models.Tokens
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val client: NetworkClient
) : AuthRepository {
    companion object {
        private const val ERROR_NETWORK = "Проверьте подключение к интернету"
        private const val ERROR_INVALID_CREDENTIALS =
            "Некорректный e-mail или пароль менее 7 символов"
        private const val ERROR_EMAIL_EXISTS = "Пользователь с таким e-mail уже существует"
        private const val ERROR_TOKEN_EXPIRED = "Токен просрочен"
        private const val ERROR_UNKNOWN = "Неизвестная ошибка"
        private const val ERROR_INCORRECT_EMAIL = "Некорректный e-mail"

        private const val SUCCESS_CODE = 200
        private const val CREATED_CODE = 201
        private const val BAD_REQUEST_CODE = 400
        private const val INCORRECT_EMAIL = 401
        private const val NOT_FOUND_CODE = 404
        private const val CONFLICT_CODE = 409
        private const val NETWORK_ERROR_CODE = -1
    }

    override suspend fun register(email: String, password: String): Resource<Tokens> {
        val response = client.doAuthRequest(AuthRequest.RegistrationRequest(email, password))

        return when (response.resultCode) {
            NETWORK_ERROR_CODE -> {
                Resource.Error<Tokens>(ERROR_NETWORK)
            }

            CREATED_CODE -> {
                with(response as AuthResponse) {
                    Resource.Success<Tokens>(Tokens(accessToken, refreshToken))
                }
            }

            BAD_REQUEST_CODE -> {
                Resource.Error<Tokens>(ERROR_INVALID_CREDENTIALS)
            }

            CONFLICT_CODE -> {
                Resource.Error<Tokens>(ERROR_EMAIL_EXISTS)
            }

            else -> {
                Resource.Error<Tokens>(ERROR_UNKNOWN)
            }
        }
    }

    override suspend fun login(email: String, password: String): Resource<Tokens> {
        val response = client.doAuthRequest(AuthRequest.LoginRequest(email, password))

        return when (response.resultCode) {
            NETWORK_ERROR_CODE -> {
                Resource.Error<Tokens>(ERROR_NETWORK)
            }

            SUCCESS_CODE -> with(response as AuthResponse) {
                Resource.Success<Tokens>(Tokens(accessToken, refreshToken))
            }

            INCORRECT_EMAIL -> {
                Resource.Error<Tokens>(ERROR_INCORRECT_EMAIL)
            }

            BAD_REQUEST_CODE -> {
                Resource.Error<Tokens>(ERROR_INVALID_CREDENTIALS)
            }

            else -> {
                Resource.Error<Tokens>(ERROR_UNKNOWN)
            }
        }
    }

    override suspend fun refresh(token: String): Resource<Tokens> {
        val response = client.doRefreshTokenRequest(RefreshTokenRequest(token))

        return when (response.resultCode) {
            NETWORK_ERROR_CODE -> {
                Resource.Error<Tokens>(ERROR_NETWORK)
            }

            SUCCESS_CODE -> with(response as RefreshTokenResponse) {
                Resource.Success<Tokens>(Tokens(accessToken, refreshToken))
            }

            else -> {
                Resource.Error<Tokens>(ERROR_UNKNOWN)
            }
        }
    }

    override suspend fun check(token: String?): Resource<Boolean> {
        val response = client.doCheckTokenRequest(CheckTokenRequest(token))

        return when (response.resultCode) {
            NETWORK_ERROR_CODE -> {
                Resource.Error<Boolean>(ERROR_NETWORK)
            }

            SUCCESS_CODE -> with(response as CheckTokenResponse) {
                Resource.Success<Boolean>(isValid)
            }

            NOT_FOUND_CODE -> {
                Resource.Error<Boolean>(ERROR_TOKEN_EXPIRED, false)
            }

            else -> {
                Resource.Error<Boolean>(ERROR_UNKNOWN, false)
            }
        }
    }

    override suspend fun recovery(email: String): Resource<Boolean> {
        val response = client.doRecoveryRequest(RecoveryRequest(email))

        return when (response.resultCode) {
            NETWORK_ERROR_CODE -> {
                Resource.Error<Boolean>(ERROR_NETWORK)
            }

            SUCCESS_CODE -> {
                Resource.Success<Boolean>(true)
            }

            else -> {
                Resource.Error<Boolean>(ERROR_UNKNOWN, false)
            }
        }
    }
}