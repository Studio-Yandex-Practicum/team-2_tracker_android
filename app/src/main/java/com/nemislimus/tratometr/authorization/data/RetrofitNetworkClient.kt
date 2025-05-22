package com.nemislimus.tratometr.authorization.data

import android.content.Context
import android.net.ConnectivityManager
import com.nemislimus.tratometr.authorization.data.dto.AuthRequest
import com.nemislimus.tratometr.authorization.data.dto.AuthResponse
import com.nemislimus.tratometr.authorization.data.dto.CheckTokenRequest
import com.nemislimus.tratometr.authorization.data.dto.CheckTokenResponse
import com.nemislimus.tratometr.authorization.data.dto.RecoveryRequest
import com.nemislimus.tratometr.authorization.data.dto.RefreshTokenRequest
import com.nemislimus.tratometr.authorization.data.dto.RefreshTokenResponse
import com.nemislimus.tratometr.authorization.data.dto.Response
import com.nemislimus.tratometr.authorization.data.network.ApiService
import com.nemislimus.tratometr.authorization.data.network.NetworkClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class RetrofitNetworkClient @Inject constructor(private val context: Context) : NetworkClient {

    companion object {
        private const val EMPTY = ""
        private const val BASE_URL = "http://130.193.44.66:8080/"
        private const val NETWORK_ERROR_CODE = -1
        private const val SERVER_ERROR_CODE = 500
        private const val NOT_FOUND_CODE = 404
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: ApiService = retrofit.create(ApiService::class.java)

    override suspend fun doAuthRequest(dto: AuthRequest): AuthResponse {
        if (!isConnected()) {
            return AuthResponse(EMPTY, EMPTY, 0).apply { resultCode = NETWORK_ERROR_CODE }
        }

        return try {
            val response = when (dto) {
                is AuthRequest.RegistrationRequest -> service.register(dto)
                is AuthRequest.LoginRequest -> service.login(dto)
            }
            if (response.isSuccessful) {
                response.body()!!.apply { resultCode = response.code() }
            } else {
                AuthResponse(EMPTY, EMPTY, 0).apply { resultCode = response.code() }
            }
        } catch (e: Exception) {
            AuthResponse(EMPTY, EMPTY, 0).apply { resultCode = SERVER_ERROR_CODE }
        }
    }

    override suspend fun doCheckTokenRequest(dto: CheckTokenRequest): CheckTokenResponse {
        if (!isConnected()) {
            return CheckTokenResponse(false).apply { resultCode = NETWORK_ERROR_CODE }
        }

        return try {
            val authHeader = "Bearer ${dto.accessToken}"
            val response = service.checkToken(authHeader)

            if (response.isSuccessful) {
                response.body()?.apply {
                    resultCode = response.code()
                }
                    ?: CheckTokenResponse(false).apply { resultCode = NOT_FOUND_CODE }
            } else {
                CheckTokenResponse(false).apply { resultCode = response.code() }
            }
        } catch (e: Exception) {
            CheckTokenResponse(false).apply { resultCode = SERVER_ERROR_CODE }
        }
    }

    override suspend fun doRefreshTokenRequest(dto: RefreshTokenRequest): RefreshTokenResponse {
        if (!isConnected()) {
            return RefreshTokenResponse(EMPTY, EMPTY).apply { resultCode = NETWORK_ERROR_CODE }
        }

        return try {
            val response = service.refreshToken(dto)

            if (response.isSuccessful) {
                response.body()!!.apply { resultCode = response.code() }
            } else {
                RefreshTokenResponse(EMPTY, EMPTY).apply { resultCode = response.code() }
            }
        } catch (e: Exception) {
            RefreshTokenResponse(EMPTY, EMPTY).apply { resultCode = SERVER_ERROR_CODE }
        }
    }

    override suspend fun doRecoveryRequest(dto: RecoveryRequest): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = NETWORK_ERROR_CODE }
        }

        return try {
            val response = service.recoverPassword(dto.email)

            if (response.isSuccessful) {
                Response().apply { resultCode = response.code() }
            } else {
                Response().apply { resultCode = response.code() }
            }
        } catch (e: Exception) {
            Response().apply { resultCode = SERVER_ERROR_CODE }
        }
    }

    private fun isConnected(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        val result = activeNetwork != null && activeNetwork.isConnected
        return activeNetwork != null && activeNetwork.isConnected
    }
}