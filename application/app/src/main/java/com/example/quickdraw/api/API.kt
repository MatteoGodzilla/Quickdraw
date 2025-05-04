package com.example.quickdraw.api

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

private const val BASE_URL = "http://192.168.1.27:12345"
//API ENDPOINTS
const val LOGIN_ENDPOINT = "$BASE_URL/auth/login"
const val REGISTER_ENDPOINT = "$BASE_URL/auth/register"

@Serializable
data class LoginRequest(val email: String, val password: String){
    fun toRequestBody(): RequestBody {
        return Json.encodeToString(this).toRequestBody("application/json".toMediaType())
    }
}

@Serializable
data class LoginResponse(val idPlayer: Int, val authToken: String)

@Serializable
data class RegisterRequest(val email: String, val password:String, val username: String){
    fun toRequestBody(): RequestBody {
        return Json.encodeToString(this).toRequestBody("application/json".toMediaType())
    }
}