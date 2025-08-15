package com.example.quickdraw.network.api

import android.util.Log
import com.example.quickdraw.TAG
import com.example.quickdraw.network.ConnectionManager
import com.example.quickdraw.network.data.InventoryResponse
import com.example.quickdraw.network.data.TokenRequest
import kotlinx.serialization.json.Json
import okhttp3.RequestBody

fun getInventoryAPI(authToken: String) : InventoryResponse?{
    val requestBody: RequestBody = TokenRequest(authToken).toRequestBody()
    val response = ConnectionManager.attempt(requestBody,INVENTORY_ENDPOINT)

    if(response!=null){
        if(response.code == 200){
            //it should always be 200, otherwise there is a problem with the auth token
            val result = response.body!!.string()
            Log.i(TAG, result)
            return Json.decodeFromString<InventoryResponse>(result)
        }
    }
    return null
}
