package com.example.quickdraw.network.api

import android.graphics.Bitmap
import android.util.Log
import com.example.quickdraw.TAG
import com.example.quickdraw.network.ConnectionManager
import com.example.quickdraw.network.data.ImageRequest
import com.example.quickdraw.network.data.ImageResponse
import kotlinx.serialization.json.Json

fun getWeaponImageAPI(id:Int) = getImage(id, IMAGE_WEAPON)
fun getBulletImageAPI(id:Int) = getImage(id, IMAGE_BULLET)
fun getMedikitImageAPI(id:Int) = getImage(id, IMAGE_MEDIKIT)
fun getUpgradeImageAPI(id:Int) = getImage(id, IMAGE_UPGRADE)
fun getPlayerImageAPI(id:Int) = getImage(id, IMAGE_PLAYER)

private fun getImage(id:Int, url: String): ImageResponse?{
     val requestBody = ImageRequest(id).toRequestBody()
     val response = ConnectionManager.attemptPost(requestBody, url)
     if(response != null && response.code == 200){
        val body = response.body.string()
        Log.i(TAG, body)
        return Json.decodeFromString<ImageResponse>(body)
     }
     return null
}