package com.example.quickdraw.network.api

import android.util.Log
import com.example.quickdraw.TAG
import com.example.quickdraw.network.ConnectionManager
import com.example.quickdraw.network.data.DuelSubmit
import com.example.quickdraw.network.data.RoundStatistics
import com.example.quickdraw.network.data.TokenRequest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder

fun submitDuelAPI(data: DuelSubmit): Boolean {
    val requestBody = data.toRequestBody()
    val response = ConnectionManager.attempt(requestBody, DUEL_ENDPOINT)
    return response?.code == 200
}

fun getRoundStatisticsAPI(authToken: String): RoundStatistics?{
    val request = TokenRequest(authToken).toRequestBody()
    val response = ConnectionManager.attempt(request, ROUND_STATISTICS)
    if(response?.code == 200){
        val body = response.body.string()
        Log.i(TAG, body)
        return Json.decodeFromString(body)
    }
    return null
}