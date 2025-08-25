package com.example.quickdraw.network.api

import android.util.Log
import com.example.quickdraw.TAG
import com.example.quickdraw.network.ConnectionManager
import com.example.quickdraw.network.data.Bandit
import com.example.quickdraw.network.data.FightBanditRequest
import com.example.quickdraw.network.data.GetBanditsResponse
import com.example.quickdraw.network.data.LeaderboardEntry
import com.example.quickdraw.network.data.Rewards
import com.example.quickdraw.network.data.TokenRequest
import kotlinx.serialization.json.Json
import okhttp3.RequestBody

fun getBanditsAPI(authToken: String): List<GetBanditsResponse> {

    val requestBody: RequestBody = TokenRequest(authToken).toRequestBody()
    val response = ConnectionManager.attempt(requestBody,BANDITS_GET)
    if(response!=null){
        if(response.code == 200){
            //it should always be 200, otherwise there is a problem with the auth token
            val result = response.body.string()
            Log.i(TAG, result)
            return Json.decodeFromString<List<GetBanditsResponse>>(result)
        }
    }
    return listOf()
}

fun fightAPI(req: FightBanditRequest): Rewards? {

    val requestBody: RequestBody = req.toRequestBody()
    val response = ConnectionManager.attempt(requestBody,BANDITS_FIGHT)
    if(response!=null){
        val result = response.body.string()
        Log.i(TAG,result)
        if(response.code == 200){
            Log.i(TAG, result)
            return Json.decodeFromString<Rewards>(result)
        }
    }
    return null
}