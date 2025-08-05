package com.example.quickdraw.network.api

import android.util.Log
import com.example.quickdraw.TAG
import com.example.quickdraw.network.data.EmployMercenary
import com.example.quickdraw.network.data.EmployMercenaryResponse
import com.example.quickdraw.network.data.HireableMercenary
import com.example.quickdraw.network.data.LeaderboardEntry
import com.example.quickdraw.network.data.MercenaryEmployedResponse
import com.example.quickdraw.network.data.MercenaryHireableResponse
import com.example.quickdraw.network.data.MercenaryUnassigned
import com.example.quickdraw.network.data.NextToUnlockMercenaryResponse
import com.example.quickdraw.network.data.TokenRequest
import com.example.quickdraw.network.data.UnassignedMercenaryResponse
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

fun getHirableAPI(authToken: String): MercenaryHireableResponse {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url(MERCENARY_HIREABLE)
        .post(TokenRequest(authToken).toRequestBody())
        .build()

    val response = client.newCall(request).execute()
    Log.i(TAG, response.code.toString())
    if(response.code == 200){
        //it should always be 200, otherwise there is a problem with the auth token
        val result = response.body!!.string()
        Log.i(TAG, result)
        return Json.decodeFromString<MercenaryHireableResponse>(result)
    }
    return MercenaryHireableResponse(mercenaries = listOf())
}

fun getUnassignedMercenariesAPI(authToken: String): UnassignedMercenaryResponse {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url(MERCENARY_PLAYER_UNASSIGNED)
        .post(TokenRequest(authToken).toRequestBody())
        .build()

    val response = client.newCall(request).execute()
    Log.i(TAG, response.code.toString())
    if(response.code == 200){
        //it should always be 200, otherwise there is a problem with the auth token
        val result = response.body!!.string()
        Log.i(TAG, result)
        return Json.decodeFromString<UnassignedMercenaryResponse>(result)
    }
    return UnassignedMercenaryResponse(mercenaries = listOf())
}

fun employMercenaryAPI(authToken: String,mercenary: HireableMercenary) : EmployMercenaryResponse {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url(MERCENARY_EMPLOY)
        .post(EmployMercenary(idMercenary = mercenary.id, authToken = authToken).toRequestBody())
        .build()

    val response = client.newCall(request).execute()
    Log.i(TAG, response.code.toString())
    if(response.code == 200){
        //it should always be 200, otherwise there is a problem with the auth token
        val result = response.body!!.string()
        Log.i(TAG, result)
        return Json.decodeFromString<EmployMercenaryResponse>(result)
    }
    return EmployMercenaryResponse(idEmployment = -1)
}

fun getNextUnlockableMercenariesAPI(authToken: String) : NextToUnlockMercenaryResponse {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url(MERCENAR_NEXT_UNLOCKABLE)
        .post(TokenRequest(authToken).toRequestBody())
        .build()

    val response = client.newCall(request).execute()
    Log.i(TAG, response.code.toString())
    if(response.code == 200){
        //it should always be 200, otherwise there is a problem with the auth token
        val result = response.body!!.string()
        Log.i(TAG, result)
        return Json.decodeFromString<NextToUnlockMercenaryResponse>(result)
    }
    return NextToUnlockMercenaryResponse(mercenaries = listOf())
}

fun getAllPlayerMercenariesAPI(authToken: String) : MercenaryEmployedResponse {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url(MERCENARY_PLAYER_ALL)
        .post(TokenRequest(authToken).toRequestBody())
        .build()

    val response = client.newCall(request).execute()
    Log.i(TAG, response.code.toString())
    if(response.code == 200){
        //it should always be 200, otherwise there is a problem with the auth token
        val result = response.body!!.string()
        Log.i(TAG, result)
        return Json.decodeFromString<MercenaryEmployedResponse>(result)
    }
    return MercenaryEmployedResponse(mercenaries = listOf())
}


