package com.example.quickdraw.network.api

import android.util.Log
import com.example.quickdraw.TAG
import com.example.quickdraw.network.ConnectionManager
import com.example.quickdraw.network.data.BuyRequest
import com.example.quickdraw.network.data.BuyUpgradeResponse
import com.example.quickdraw.network.data.TokenRequest
import com.example.quickdraw.network.data.ShopBullet
import com.example.quickdraw.network.data.ShopMedikit
import com.example.quickdraw.network.data.ShopUpgrade
import com.example.quickdraw.network.data.ShopWeapon
import kotlinx.serialization.json.Json
import okhttp3.RequestBody

fun getShopWeaponsAPI(authToken: String): List<ShopWeapon> {
    val requestBody: RequestBody = TokenRequest(authToken).toRequestBody()
    val response = ConnectionManager.attempt(requestBody,SHOP_WEAPONS)
    if(response!=null){
        if(response.code == 200){
            //it should always be 200, otherwise there is a problem with the auth token
            val result = response.body!!.string()
            Log.i(TAG, result)
            return Json.decodeFromString<List<ShopWeapon>>(result)
        }
    }
    return listOf()
}

fun getShopBulletsAPI(authToken: String): List<ShopBullet> {
    val requestBody: RequestBody = TokenRequest(authToken).toRequestBody()
    val response = ConnectionManager.attempt(requestBody,SHOP_BULLETS)
    if(response!=null){
        if(response.code == 200){
            //it should always be 200, otherwise there is a problem with the auth token
            val result = response.body!!.string()
            Log.i(TAG, result)
            return Json.decodeFromString<List<ShopBullet>>(result)
        }
    }
    return listOf()
}

fun getShopMedikitsAPI(authToken: String): List<ShopMedikit> {
    val requestBody: RequestBody = TokenRequest(authToken).toRequestBody()
    val response = ConnectionManager.attempt(requestBody,SHOP_MEDIKITS)
    if(response!=null){
        if(response.code == 200){
            //it should always be 200, otherwise there is a problem with the auth token
            val result = response.body!!.string()
            Log.i(TAG, result)
            return Json.decodeFromString<List<ShopMedikit>>(result)
        }
    }
    return listOf()
}

fun getShopUpgradesAPI(authToken: String): List<ShopUpgrade> {
    val requestBody: RequestBody = TokenRequest(authToken).toRequestBody()
    val response = ConnectionManager.attempt(requestBody,SHOP_UPGRADES)
    if(response!=null){
        if(response.code == 200){
            //it should always be 200, otherwise there is a problem with the auth token
            val result = response.body!!.string()
            Log.i(TAG, result)
            return Json.decodeFromString<List<ShopUpgrade>>(result)
        }
    }
    return listOf()
}

fun buyBulletsAPI(buy: BuyRequest): ShopBullet?{
    val requestBody: RequestBody = buy.toRequestBody()
    val response = ConnectionManager.attempt(requestBody,SHOP_BUY_BULLETS)
    if(response!=null){
        if(response.code == 200){
            //it should always be 200, otherwise there is a problem with the auth token
            val result = response.body!!.string()
            Log.i(TAG, result)
            return Json.decodeFromString<ShopBullet>(result)
        }
    }
    return null
}

fun buyMedikitAPI(buy: BuyRequest): ShopMedikit?{
    val requestBody: RequestBody = buy.toRequestBody()
    val response = ConnectionManager.attempt(requestBody,SHOP_BUY_MEDIKIT)
    if(response!=null){
        if(response.code == 200){
            //it should always be 200, otherwise there is a problem with the auth token
            val result = response.body!!.string()
            Log.i(TAG, result)
            return Json.decodeFromString<ShopMedikit>(result)
        }
    }
    return null
}

fun buyWeaponAPI(buy: BuyRequest): ShopWeapon?{
    val requestBody: RequestBody = buy.toRequestBody()
    val response = ConnectionManager.attempt(requestBody,SHOP_BUY_WEAPON)
    if(response!=null){
        if(response.code == 200){
            //it should always be 200, otherwise there is a problem with the auth token
            val result = response.body!!.string()
            Log.i(TAG, result)
            return Json.decodeFromString<ShopWeapon>(result)
        }
    }
    return null
}

fun buyUpgradeAPI(buy: BuyRequest): BuyUpgradeResponse?{
    val requestBody: RequestBody = buy.toRequestBody()
    val response = ConnectionManager.attempt(requestBody,SHOP_BUY_UPGRADE)
    if(response!=null){
        if(response.code == 200){
            //it should always be 200, otherwise there is a problem with the auth token
            val result = response.body!!.string()
            Log.i(TAG, result)
            return Json.decodeFromString<BuyUpgradeResponse>(result)
        }
    }
    return null
}

