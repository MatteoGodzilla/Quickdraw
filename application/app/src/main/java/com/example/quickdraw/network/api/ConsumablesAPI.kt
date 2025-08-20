package com.example.quickdraw.network.api

import com.example.quickdraw.network.ConnectionManager
import com.example.quickdraw.network.data.UseMedikitRequest
import com.example.quickdraw.network.data.UseMedikitResponse
import kotlinx.serialization.json.Json

fun useMedikitAPI(authToken: String, medikitType: Int): UseMedikitResponse?{
    val body = UseMedikitRequest(authToken, medikitType).toRequestBody()
    val response = ConnectionManager.attempt(body, USE_MEDIKIT)
    if(response != null && response.code == 200) {
        val resBody = response.body.string()
        return Json.decodeFromString<UseMedikitResponse>(resBody)
    }
    return null
}