package com.example.quickdraw.network.api

import android.util.Log
import com.example.quickdraw.TAG
import com.example.quickdraw.network.ConnectionManager
import com.example.quickdraw.network.data.LeaderboardEntry
import com.example.quickdraw.network.data.TokenRequest
import kotlinx.serialization.json.Json
import okhttp3.RequestBody

fun getFriendLeaderboardAPI(authToken: String): List<LeaderboardEntry> {

    val requestBody: RequestBody = TokenRequest(authToken).toRequestBody()
    val response = ConnectionManager.attemptPost(requestBody,BOUNTY_FRIENDS)

    if(response!=null){
        if(response.code == 200){
            //it should always be 200, otherwise there is a problem with the auth token
            val result = response.body!!.string()
            Log.i(TAG, result)
            return Json.decodeFromString<List<LeaderboardEntry>>(result)
        }
    }
    return listOf()
}

fun getGlobalLeaderboardAPI(): List<LeaderboardEntry> {
    val response = ConnectionManager.attemptGet(BOUNTY_LEADERBOARD)
    if(response!=null){
        if(response.code == 200){
            //it should always be 200, otherwise there is a problem with the auth token
            val result = response.body!!.string()
            Log.i(TAG, result)
            return Json.decodeFromString<List<LeaderboardEntry>>(result)
        }
        else{
            Log.i(TAG,response.code.toString())
        }
    }
    return listOf()
}