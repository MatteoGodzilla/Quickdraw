package com.example.quickdraw.network.api

import android.util.Log
import com.example.quickdraw.TAG
import com.example.quickdraw.network.ConnectionManager
import com.example.quickdraw.network.data.PlayerStatus
import com.example.quickdraw.network.data.TokenRequest
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

fun getStatusAPI(authToken: String): PlayerStatus? {
    val requestBody: RequestBody = TokenRequest(authToken).toRequestBody()
    val response = ConnectionManager.AttemptQuery(requestBody,STATUS_ENDPOINT)
    if(response!=null){
        if(response.code == 200){
            //it should always be 200, otherwise there is a problem with the auth token
            val result = response.body!!.string()
            Log.i(TAG, result)
            return Json.decodeFromString<PlayerStatus>(result)
        }
    }
    return null
}

fun getLevelsAPI(): List<Int> {
    val requestBody: RequestBody = "".toRequestBody()
    val response = ConnectionManager.AttemptQuery(requestBody,LEVELS_ENDPOINT)
    if(response!=null){
        if(response.code == 200){
            //it should always be 200, otherwise there is a problem with the auth token
            val result = response.body!!.string()
            Log.i(TAG, result)
            return Json.decodeFromString<List<Int>>(result)
        }
    }
    return listOf()
}