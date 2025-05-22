package com.nemislimus.tratometr.authorization.data.network

import com.nemislimus.tratometr.authorization.data.dto.AuthRequest
import com.nemislimus.tratometr.authorization.data.dto.AuthResponse
import com.nemislimus.tratometr.authorization.data.dto.CheckTokenResponse
import com.nemislimus.tratometr.authorization.data.dto.RefreshTokenRequest
import com.nemislimus.tratometr.authorization.data.dto.RefreshTokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("auth/registration")
    suspend fun register(@Body request: AuthRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body request: AuthRequest): Response<AuthResponse>

    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<RefreshTokenResponse>

    @GET("auth/check")
    suspend fun checkToken(@Header("Authorization") authHeader: String): Response<CheckTokenResponse>

    @POST("auth/recovery")
    suspend fun recoverPassword(@Header("email") email: String): Response<Unit>
}