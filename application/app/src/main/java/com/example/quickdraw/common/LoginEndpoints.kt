package com.example.quickdraw.common

import kotlinx.serialization.Serializable

//API ENDPOINTS
const val LOGIN_ENDPOINT = "$BASE_URL/auth/login"
const val TOKEN_LOGIN_ENDPOINT = "$BASE_URL/auth/tokenLogin"
const val REGISTER_ENDPOINT = "$BASE_URL/auth/register"

/* --------------REQUEST-------------- */
@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class TokenRequest(val authToken: String)

@Serializable
data class RegisterRequest(val email: String, val password:String, val username: String)

/* --------------RESPONSE------------- */

@Serializable
data class LoginResponse(val authToken: String)
