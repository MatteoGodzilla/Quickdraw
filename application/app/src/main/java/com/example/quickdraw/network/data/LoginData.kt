package com.example.quickdraw.network.data

import kotlinx.serialization.Serializable


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
