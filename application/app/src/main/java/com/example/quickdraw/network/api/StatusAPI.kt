package com.example.quickdraw.network.api

import android.util.Log
import com.example.quickdraw.TAG
import com.example.quickdraw.network.ConnectionManager
import com.example.quickdraw.network.data.BaseStats
import com.example.quickdraw.network.data.PlayerInfo
import com.example.quickdraw.network.data.TokenRequest
import kotlinx.serialization.json.Json
import okhttp3.RequestBody

fun getStatusAPI(authToken: String): PlayerInfo? {
    val requestBody: RequestBody = TokenRequest(authToken).toRequestBody()
    val response = ConnectionManager.attempt(requestBody,STATUS_ENDPOINT)
    if(response!=null){
        if(response.code == 200){
            //it should always be 200, otherwise there is a problem with the auth token
            val result = response.body.string()
            Log.i(TAG, result)
            return Json.decodeFromString<PlayerInfo>(result)
        }
    }
    return null
}

fun getLevelsAPI(): List<Int> {
    val requestBody: RequestBody = "".toRequestBody()
    val response = ConnectionManager.attempt(requestBody,LEVELS_ENDPOINT,false)
    if(response!=null){
        if(response.code == 200){
            //it should always be 200, otherwise there is a problem with the auth token
            val result = response.body.string()
            Log.i(TAG, "Levels:$result")
            return Json.decodeFromString<List<Int>>(result)
        }
        Log.i(TAG, "Failed to fetch levels,${response.code}")
    }
    return listOf()
}

fun getBaseAPI(): List<BaseStats> {
    val requestBody: RequestBody = "".toRequestBody()
    val response = ConnectionManager.attempt(requestBody,BASE_STATS_ENDPOINT,false)
    if(response!=null){
        if(response.code == 200){
            //it should always be 200, otherwise there is a problem with the auth token
            val result = response.body.string()
            Log.i(TAG, "stats:$result")
            return Json.decodeFromString<List<BaseStats>>(result)
        }
        Log.i(TAG, "Failed to fetch levels,${response.code}")
    }
    return listOf()
}