package com.nemislimus.tratometr.authorization.data.network

import com.nemislimus.tratometr.authorization.data.dto.AuthRequest
import com.nemislimus.tratometr.authorization.data.dto.CheckTokenRequest
import com.nemislimus.tratometr.authorization.data.dto.RecoveryRequest
import com.nemislimus.tratometr.authorization.data.dto.RefreshTokenRequest
import com.nemislimus.tratometr.authorization.data.dto.Response

interface NetworkClient {
    suspend fun doAuthRequest(dto: AuthRequest): Response
    suspend fun doRefreshTokenRequest(dto: RefreshTokenRequest): Response
    suspend fun doCheckTokenRequest(dto: CheckTokenRequest): Response
    suspend fun doRecoveryRequest(dto: RecoveryRequest): Response
}