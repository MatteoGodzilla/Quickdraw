package com.example.quickdraw.network.api

import android.util.Log
import com.example.quickdraw.TAG
import com.example.quickdraw.network.data.PlayerStatus
import com.example.quickdraw.network.data.TokenRequest
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

fun getStatusAPI(authToken: String): PlayerStatus? {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url(STATUS_ENDPOINT)
        .post(TokenRequest(authToken).toRequestBody())
        .build()

    val response = client.newCall(request).execute()
    Log.i(TAG, response.code.toString())
    if(response.code == 200){
        //it should always be 200, otherwise there is a problem with the auth token
        val result = response.body!!.string()
        Log.i(TAG, result)
        return Json.decodeFromString<PlayerStatus>(result)
    }
    return null
}

fun getLevelsAPI(): List<Int> {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url(LEVELS_ENDPOINT)
        .build()

    val response = client.newCall(request).execute()
    Log.i(TAG, response.code.toString())
    if(response.code == 200){
        //it should always be 200, otherwise there is a problem with the auth token
        val result = response.body!!.string()
        Log.i(TAG, result)
        return Json.decodeFromString<List<Int>>(result)
    }
    return listOf()
}