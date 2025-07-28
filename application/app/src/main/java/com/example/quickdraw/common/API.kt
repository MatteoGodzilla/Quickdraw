package com.example.quickdraw.common

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

const val BASE_URL = "http://192.168.1.59:8000"


inline fun <reified T> T.toRequestBody(): RequestBody {
    val jsonString = Json.encodeToString(this)
    return jsonString.toRequestBody("application/json".toMediaType())
}

