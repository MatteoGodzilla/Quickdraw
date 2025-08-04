package com.example.quickdraw.network.api

import android.util.Log
import com.example.quickdraw.TAG
import com.example.quickdraw.network.data.ActiveContract
import com.example.quickdraw.network.data.ActiveContractResponse
import com.example.quickdraw.network.data.AvailableContract
import com.example.quickdraw.network.data.AvailableContractResponse
import com.example.quickdraw.network.data.ContractRedeemRequest
import com.example.quickdraw.network.data.ContractRedeemResponse
import com.example.quickdraw.network.data.ContractStartRequest
import com.example.quickdraw.network.data.TokenRequest
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

fun redeemContractAPI(authToken: String, contract: ActiveContract) : Boolean {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url(CONTRACTS_REDEEM)
        .post(ContractRedeemRequest(authToken, contract.activeId).toRequestBody())
        .build()

    val response = client.newCall(request).execute()
    Log.i(TAG, response.code.toString())
    if(response.code == 200){
        val body = response.body!!.string()
        Log.i(TAG, body)
        val obj = Json.decodeFromString<ContractRedeemResponse>(body)
        return obj.success
    }
    return false
}

fun startContractAPI(authToken: String, contract: AvailableContract): Boolean{
    val client = OkHttpClient()
    val request = Request.Builder()
        .url(CONTRACTS_START)
        //TODO: add chosen mercenaries to request
        .post(ContractStartRequest(authToken, contract.id, listOf(1)).toRequestBody())
        .build()

    val response = client.newCall(request).execute()
    Log.i(TAG, response.code.toString())
    val body = response.body!!.string()
    Log.i(TAG, body)
    return response.code == 200
}

fun getActiveContractsAPI(authToken: String): List<ActiveContract>{
    val client = OkHttpClient()
    val request = Request.Builder()
        .url(CONTRACTS_ACTIVE_ENDPOINT)
        .post(TokenRequest(authToken).toRequestBody())
        .build()

    val response = client.newCall(request).execute()
    Log.i(TAG, response.code.toString())
    if(response.code == 200){
        //it should always be 200, otherwise there is a problem with the auth token
        val result = response.body!!.string()
        Log.i(TAG, result)
        return Json.decodeFromString<ActiveContractResponse>(result).contracts
    }
    return listOf()
}

fun getAvailableContractsAPI(authToken: String): List<AvailableContract> {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url(CONTRACTS_AVAILABLE_ENDPOINT)
        .post(TokenRequest(authToken).toRequestBody())
        .build()

    val response = client.newCall(request).execute()
    Log.i(TAG, response.code.toString())
    if(response.code == 200){
        //it should always be 200, otherwise there is a problem with the auth token
        val result = response.body!!.string()
        Log.i(TAG, result)
        return Json.decodeFromString<AvailableContractResponse>(result).contracts
    }
    return listOf()
}
