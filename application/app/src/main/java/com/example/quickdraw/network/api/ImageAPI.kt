package com.example.quickdraw.network.api

import android.util.Log
import com.example.quickdraw.TAG
import com.example.quickdraw.network.ConnectionManager
import com.example.quickdraw.network.data.ImageRequest
import com.example.quickdraw.network.data.ImageResponse
import com.example.quickdraw.network.data.UpdateProfileRequest
import kotlinx.serialization.json.Json

fun getWeaponImageAPI(id:Int) = getImage(id, IMAGE_WEAPON)
fun getBulletImageAPI(id:Int) = getImage(id, IMAGE_BULLET)
fun getMedikitImageAPI(id:Int) = getImage(id, IMAGE_MEDIKIT)
fun getUpgradeImageAPI(id:Int) = getImage(id, IMAGE_UPGRADE)
fun getPlayerImageAPI(id:Int) = getImage(id, IMAGE_PLAYER)

private fun getImage(id:Int, url: String): ImageResponse?{
     val requestBody = ImageRequest(id).toRequestBody()
     val response = ConnectionManager.attempt(requestBody, url)
     if(response != null && response.code == 200){
        val body = response.body.string()
        Log.i(TAG, body)
        return Json.decodeFromString<ImageResponse>(body)
     }
     return null
}

fun updateProfilePicAPI(authToken: String, encoded: String) : Boolean{
    val requestBody = UpdateProfileRequest(authToken, encoded).toRequestBody()
    val response = ConnectionManager.attempt(requestBody, IMAGE_UPDATE_PLAYER_PIC)
    return response?.code == 200
}