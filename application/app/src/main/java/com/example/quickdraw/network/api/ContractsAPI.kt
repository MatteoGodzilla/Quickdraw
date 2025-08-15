package com.example.quickdraw.network.api

import android.util.Log
import com.example.quickdraw.TAG
import com.example.quickdraw.network.ConnectionManager
import com.example.quickdraw.network.data.ActiveContract
import com.example.quickdraw.network.data.ActiveContractResponse
import com.example.quickdraw.network.data.AvailableContract
import com.example.quickdraw.network.data.AvailableContractResponse
import com.example.quickdraw.network.data.ContractRedeemRequest
import com.example.quickdraw.network.data.ContractRedeemResponse
import com.example.quickdraw.network.data.ContractStartRequest
import com.example.quickdraw.network.data.ContractStartResponse
import com.example.quickdraw.network.data.StartedContract
import com.example.quickdraw.network.data.TokenRequest
import kotlinx.serialization.json.Json
import okhttp3.RequestBody

fun redeemContractAPI(authToken: String, contract: ActiveContract) : ContractRedeemResponse {
    val requestBody: RequestBody = ContractRedeemRequest(authToken, contract.activeId).toRequestBody()
    val response = ConnectionManager.attempt(requestBody,CONTRACTS_REDEEM)
    if(response!=null){
        Log.i(TAG, response.code.toString())
        if(response.code == 200){
            val body = response.body.string()
            Log.i(TAG, body)
            val obj = Json.decodeFromString<ContractRedeemResponse>(body)
            return obj
        }
    }
    return ContractRedeemResponse(success = false, reward = 0, returnableContract = AvailableContract(0,"",0,0,0,0))
}

fun startContractAPI(authToken: String, contract: AvailableContract,mercenaries:List<Int>): ContractStartResponse{
    val requestBody: RequestBody = ContractStartRequest(authToken, contract.id, mercenaries).toRequestBody()
    val response = ConnectionManager.attempt(requestBody,CONTRACTS_START)
    if(response!=null){
        if(response.code == 200){
            //it should always be 200, otherwise there is a problem with the auth token
            val body = response.body.string()
            Log.i(TAG, body)
            return Json.decodeFromString<ContractStartResponse>(body)
        }
    }
    return ContractStartResponse(success = false, contractInfo = StartedContract(0,0))
}

fun getActiveContractsAPI(authToken: String): List<ActiveContract>{
    val requestBody: RequestBody = TokenRequest(authToken).toRequestBody()
    val response = ConnectionManager.attempt(requestBody,CONTRACTS_ACTIVE_ENDPOINT)
    if(response!=null){
        if(response.code == 200){
            //it should always be 200, otherwise there is a problem with the auth token
            val result = response.body.string()
            Log.i(TAG, result)
            return Json.decodeFromString<ActiveContractResponse>(result).contracts
        }
    }
    return listOf()
}

fun getAvailableContractsAPI(authToken: String): List<AvailableContract> {
    val requestBody: RequestBody = TokenRequest(authToken).toRequestBody()
    val response = ConnectionManager.attempt(requestBody,CONTRACTS_AVAILABLE_ENDPOINT)

    if(response!=null){
        if(response.code == 200){
            //it should always be 200, otherwise there is a problem with the auth token
            val result = response.body.string()
            Log.i(TAG, result)
            return Json.decodeFromString<AvailableContractResponse>(result).contracts
        }
    }
    return listOf()
}
