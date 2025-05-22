package com.nemislimus.tratometr.authorization.domain.models

data class Tokens(
    val accessToken: String?,
    val refreshToken: String?
)
