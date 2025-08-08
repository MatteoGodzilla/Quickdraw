package com.example.quickdraw.network.api

import android.util.Log
import com.example.quickdraw.TAG
import com.example.quickdraw.game.dataDisplayers.BulletShopEntry
import com.example.quickdraw.network.data.BuyRequest
import com.example.quickdraw.network.data.TokenRequest
import com.example.quickdraw.network.data.ShopBullet
import com.example.quickdraw.network.data.ShopMedikit
import com.example.quickdraw.network.data.ShopUpgrade
import com.example.quickdraw.network.data.ShopWeapon
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

fun getShopWeaponsAPI(authToken: String): List<ShopWeapon> {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url(SHOP_WEAPONS)
        .post(TokenRequest(authToken).toRequestBody())
        .build()

    val response = client.newCall(request).execute()
    Log.i(TAG, response.code.toString())
    if(response.code == 200){
        //it should always be 200, otherwise there is a problem with the auth token
        val result = response.body!!.string()
        Log.i(TAG, result)
        return Json.decodeFromString<List<ShopWeapon>>(result)
    }
    return listOf()
}

fun getShopBulletsAPI(authToken: String): List<ShopBullet> {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url(SHOP_BULLETS)
        .post(TokenRequest(authToken).toRequestBody())
        .build()

    val response = client.newCall(request).execute()
    Log.i(TAG, response.code.toString())
    if(response.code == 200){
        //it should always be 200, otherwise there is a problem with the auth token
        val result = response.body!!.string()
        Log.i(TAG, result)
        return Json.decodeFromString<List<ShopBullet>>(result)
    }
    return listOf()
}

fun getShopMedikitsAPI(authToken: String): List<ShopMedikit> {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url(SHOP_MEDIKITS)
        .post(TokenRequest(authToken).toRequestBody())
        .build()

    val response = client.newCall(request).execute()
    Log.i(TAG, response.code.toString())
    if(response.code == 200){
        //it should always be 200, otherwise there is a problem with the auth token
        val result = response.body!!.string()
        Log.i(TAG, result)
        return Json.decodeFromString<List<ShopMedikit>>(result)
    }
    return listOf()
}

fun getShopUpgradesAPI(authToken: String): List<ShopUpgrade> {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url(SHOP_UPGRADES)
        .post(TokenRequest(authToken).toRequestBody())
        .build()

    val response = client.newCall(request).execute()
    Log.i(TAG, response.code.toString())
    if(response.code == 200){
        //it should always be 200, otherwise there is a problem with the auth token
        val result = response.body!!.string()
        Log.i(TAG, result)
        return Json.decodeFromString<List<ShopUpgrade>>(result)
    }
    return listOf()
}

fun buyBulletsAPI(buy: BuyRequest): ShopBullet?{
    val client = OkHttpClient()
    val request = Request.Builder()
        .url(SHOP_BUY_BULLETS)
        .post(buy.toRequestBody())
        .build()

    val response = client.newCall(request).execute()
    Log.i(TAG, response.code.toString())
    if(response.code == 200){
        //it should always be 200, otherwise there is a problem with the auth token
        val result = response.body!!.string()
        Log.i(TAG, result)
        return Json.decodeFromString<ShopBullet>(result)
    }
    return null
}
