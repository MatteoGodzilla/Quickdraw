package com.example.quickdraw.common

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

private const val BASE_URL = "http://192.168.1.59:8000"
//API ENDPOINTS
const val LOGIN_ENDPOINT = "$BASE_URL/auth/login"
const val TOKEN_LOGIN_ENDPOINT = "$BASE_URL/auth/tokenLogin"
const val REGISTER_ENDPOINT = "$BASE_URL/auth/register"

/* --------------REQUEST--------------*/

inline fun <reified T> T.toRequestBody(): RequestBody {
    val jsonString = Json.encodeToString(this)
    return jsonString.toRequestBody("application/json".toMediaType())
}

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class TokenLoginRequest(val id: Int, val token: String)

@Serializable
data class RegisterRequest(val email: String, val password:String, val username: String)

/* --------------RESPONSE-------------*/

@Serializable
data class LoginResponse(val authToken: String)
