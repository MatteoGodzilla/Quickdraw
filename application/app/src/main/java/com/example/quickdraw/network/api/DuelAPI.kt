package com.example.quickdraw.network.api

import com.example.quickdraw.network.ConnectionManager
import com.example.quickdraw.network.data.DuelSubmit

fun submitDuelAPI(data: DuelSubmit): Boolean {
    val requestBody = data.toRequestBody()
    val response = ConnectionManager.attempt(requestBody, DUEL_ENDPOINT)
    return response?.code == 200
}