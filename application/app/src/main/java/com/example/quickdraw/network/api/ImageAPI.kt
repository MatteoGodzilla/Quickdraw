package com.example.quickdraw.network.api

import com.example.quickdraw.network.ConnectionManager
import com.example.quickdraw.network.data.ImageRequest
import com.example.quickdraw.network.data.ImageResponse
import com.example.quickdraw.network.data.UpdateProfileRequest
import kotlinx.serialization.json.Json

fun getWeaponImageAPI(id:Int) = getImage(id, IMAGE_WEAPON)
fun getBulletShopImageAPI2(id:Int) = getImage(id, IMAGE_BULLET_SHOP)
fun getMedikitShopImageAPI2(id:Int) = getImage(id, IMAGE_MEDIKIT_SHOP)
fun getUpgradeImageAPI(id:Int) = getImage(id, IMAGE_UPGRADE)
fun getPlayerImageAPI(id:Int) = getImage(id, IMAGE_PLAYER)
fun getMercenaryImage(id:Int) = getImage(id, IMAGE_MERCENARY)
fun getBanditImage(id:Int) = getImage(id,IMAGE_BANDIT)
fun getBulletTypeImageAPI(id:Int) = getImage(id, IMAGE_BULLET_TYPE)
fun getMedikitTypeImageAPI(id:Int) = getImage(id, IMAGE_MEDIKIT_TYPE)

private fun getImage(id:Int, url: String): ImageResponse?{
     val requestBody = ImageRequest(id).toRequestBody()
     val response = ConnectionManager.attempt(requestBody, url,true,5000)
     if(response != null && response.code == 200){
        val body = response.body.string()
        return Json.decodeFromString<ImageResponse>(body)
     }
     return null
}

fun updateProfilePicAPI(authToken: String, encoded: String) : Boolean{
    val requestBody = UpdateProfileRequest(authToken, encoded).toRequestBody()
    val response = ConnectionManager.attempt(requestBody, IMAGE_UPDATE_PLAYER_PIC)
    return response?.code == 200
}
