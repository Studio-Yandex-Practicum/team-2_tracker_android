package com.nemislimus.tratometr.authorization.data.dto

sealed class AuthRequest(val email: String, val password: String) {
    class RegistrationRequest(email: String, password: String) : AuthRequest(email, password)
    class LoginRequest(email: String, password: String) : AuthRequest(email, password)
}