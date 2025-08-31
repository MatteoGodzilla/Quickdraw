package com.example.quickdraw

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Base64.DEFAULT
import android.util.Log
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import com.example.quickdraw.game.repo.GameRepository
import com.example.quickdraw.network.api.getBanditImage
import com.example.quickdraw.network.api.getBulletShopImageAPI2
import com.example.quickdraw.network.api.getBulletTypeImageAPI
import com.example.quickdraw.network.api.getMedikitShopImageAPI2
import com.example.quickdraw.network.api.getMedikitTypeImageAPI
import com.example.quickdraw.network.api.getMercenaryImage
import com.example.quickdraw.network.api.getPlayerImageAPI
import com.example.quickdraw.network.api.getUpgradeImageAPI
import com.example.quickdraw.network.api.getWeaponImageAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class ImageLoader(private val context: Context) {

    private fun buildNotFound(): ByteArray{
        var imageNotFound : Bitmap = AppCompatResources.getDrawable(context, R.drawable.question_mark_24px)!!.toBitmap(512, 512)
        val bitmap = imageNotFound.asImageBitmap().asAndroidBitmap() // Convert ImageBitmap to Android Bitmap
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream) // Compress to PNG/JPEG
        return stream.toByteArray()
    }
    var notFound: ByteArray = buildNotFound()

    private val localScope = CoroutineScope(Dispatchers.IO)

    private val weaponCache: MutableMap<Int,MutableStateFlow<ByteArray>> = mutableMapOf()
    private val bulletShopCache: MutableMap<Int,MutableStateFlow<ByteArray>> = mutableMapOf()
    private val medikitShopCache: MutableMap<Int,MutableStateFlow<ByteArray>> = mutableMapOf()
    private val upgradeCache: MutableMap<Int,MutableStateFlow<ByteArray>> = mutableMapOf()
    private val playerCache: MutableMap<Int,MutableStateFlow<ByteArray>> = mutableMapOf()
    private val banditCache: MutableMap<Int, MutableStateFlow<ByteArray>> = mutableMapOf()
    private val mercenaryCache: MutableMap<Int, MutableStateFlow<ByteArray>> = mutableMapOf()
    private val bulletTypeCache: MutableMap<Int,MutableStateFlow<ByteArray>> = mutableMapOf()
    private val medikitTypeCache: MutableMap<Int,MutableStateFlow<ByteArray>> = mutableMapOf()

    private val weaponLoading: MutableMap<Int,Boolean> = mutableMapOf()
    private val bulletLoading: MutableMap<Int,Boolean> = mutableMapOf()
    private val medikitLoading: MutableMap<Int,Boolean> = mutableMapOf()
    private val upgradeLoading: MutableMap<Int,Boolean> = mutableMapOf()
    private val playerLoading: MutableMap<Int,Boolean> = mutableMapOf()
    private val bulletTypeLoading: MutableMap<Int,Boolean> = mutableMapOf()
    private val medikitTypeLoading: MutableMap<Int,Boolean> = mutableMapOf()

    fun getWeaponFlow(id: Int): MutableStateFlow<ByteArray> {
        if(!weaponCache.containsKey(id)) {
            weaponCache[id] = MutableStateFlow(notFound)
            localScope.launch{
                Log.i(TAG, "[ImageLoader] Weapon $id")
                val response = getWeaponImageAPI(id)
                if(response != null){
                    val bytes = Base64.decode(response.image, DEFAULT)
                    weaponCache[id]!!.value = bytes
                }
            }
        }
        return weaponCache[id]!!
    }

    fun getBulletShopFlow(id: Int): MutableStateFlow<ByteArray> {
        if(!bulletShopCache.containsKey(id)) {
            bulletShopCache[id] = MutableStateFlow(notFound)
            localScope.launch{
                Log.i(TAG, "[ImageLoader] Bullet $id")
                val response = getBulletShopImageAPI2(id)
                if(response != null){
                    val bytes = Base64.decode(response.image, DEFAULT)
                    bulletShopCache[id]!!.value = bytes
                }
            }
        }
        return bulletShopCache[id]!!
    }

    fun getMedikitShopFlow(id: Int): MutableStateFlow<ByteArray> {
        if(!medikitShopCache.containsKey(id)) {
            medikitShopCache[id] = MutableStateFlow(notFound)
            localScope.launch{
                Log.i(TAG, "[ImageLoader] Medikit $id")
                val response = getMedikitShopImageAPI2(id)
                if(response != null){
                    val bytes = Base64.decode(response.image, DEFAULT)
                    medikitShopCache[id]!!.value = bytes
                }
            }
        }
        return medikitShopCache[id]!!
    }

    fun getUpgradeFlow(id: Int): MutableStateFlow<ByteArray> {
        if(!upgradeCache.containsKey(id)) {
            upgradeCache[id] = MutableStateFlow(notFound)
            localScope.launch{
                val response = getUpgradeImageAPI(id)
                if(response != null){
                    val bytes = Base64.decode(response.image, DEFAULT)
                    upgradeCache[id]!!.value = bytes
                }
            }
        }
        return upgradeCache[id]!!
    }

    fun getPlayerFlow(id: Int): MutableStateFlow<ByteArray> {
        if(!playerCache.containsKey(id)) {
            playerCache[id] = MutableStateFlow(notFound)
            localScope.launch{
                Log.i(TAG, "[ImageLoader] Player $id")
                val response = getPlayerImageAPI(id)
                if(response != null){
                    val bytes = Base64.decode(response.image, DEFAULT)
                    val result = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
                    playerCache[id]!!.value = bytes
                }
            }
        }
        return playerCache[id]!!
    }

    fun getBanditFlow(id: Int): MutableStateFlow<ByteArray>{
        if(!banditCache.containsKey(id)) {
            banditCache[id] = MutableStateFlow(notFound)
            localScope.launch{
                Log.i(TAG, "[ImageLoader] Mercenary $id")
                val response = getBanditImage(id)
                if(response != null){
                    val bytes = Base64.decode(response.image, DEFAULT)
                    val result = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
                    banditCache[id]!!.value = bytes
                }
            }
        }
        return banditCache[id]!!
    }

    fun getMercenaryFlow(id: Int): MutableStateFlow<ByteArray> {
        if (!mercenaryCache.containsKey(id)) {
            mercenaryCache[id] = MutableStateFlow(notFound)
            localScope.launch {
                Log.i(TAG, "[ImageLoader] Mercenary $id")
                val response = getMercenaryImage(id)
                if (response != null) {
                    val bytes = Base64.decode(response.image, DEFAULT)
                    val result = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    mercenaryCache[id]!!.value = bytes
                }
            }
        }
        return mercenaryCache[id]!!
    }

    fun getBulletTypeFlow(id: Int): MutableStateFlow<ByteArray> {
        if(!bulletTypeCache.containsKey(id)) {
            bulletTypeCache[id] = MutableStateFlow(notFound)
            localScope.launch{
                bulletTypeLoading[id] = true
                Log.i(TAG, "[ImageLoader] Player $id")
                val response = getBulletTypeImageAPI(id)
                if(response != null){
                    val bytes = Base64.decode(response.image, DEFAULT)
                    val result = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
                    bulletTypeCache[id]!!.value = bytes
                }
                bulletTypeLoading[id] = false
            }
        }
        return bulletTypeCache[id]!!
    }

    fun getMedikitTypeFlow(id: Int): MutableStateFlow<ByteArray> {
        if(!medikitTypeCache.containsKey(id)) {
            medikitTypeCache[id] = MutableStateFlow(notFound)
            localScope.launch{
                medikitTypeLoading[id] = true
                Log.i(TAG, "[ImageLoader] Player $id")
                val response = getMedikitTypeImageAPI(id)
                if(response != null){
                    val bytes = Base64.decode(response.image, DEFAULT)
                    val result = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
                    medikitTypeCache[id]!!.value = bytes
                }
                medikitTypeLoading[id] = false
            }
        }
        return medikitTypeCache[id]!!
    }

    fun invalidatePlayerImage(id: Int){
        playerCache.remove(id)
    }

    /*
    suspend fun loadFrom(repo: GameRepository){
        getPlayerFlow(repo.player.player.value.id)
        //shop bullets
        for(bullet in repo.shop.bullets.value){
            getBulletShopFlow(bullet.id)
        }
        //shop weapons
        for(w in repo.shop.weapons.value){
            getWeaponFlow(w.id)
        }
        //shop medikits
        for(m in repo.shop.medikits.value){
            getMedikitShopFlow(m.id)
        }
        //shop upgrades
        for(u in repo.shop.upgrades.value){
            getUpgradeFlow(u.id)
        }
        //bounty leaderboard
        for(p in repo.leaderboard.global.value){
            getPlayerFlow(p.id )
        }

        //mercenaries
        for(m in repo.mercenaries.hireable.value){
            getMercenaryFlow(m.id)
        }
    }

     */
}
