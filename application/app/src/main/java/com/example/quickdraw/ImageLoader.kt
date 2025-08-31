package com.example.quickdraw

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Base64.DEFAULT
import android.util.Log
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import com.example.quickdraw.game.repo.GameRepository
import com.example.quickdraw.network.api.getBanditImage
import com.example.quickdraw.network.api.getBulletImageAPI
import com.example.quickdraw.network.api.getMedikitImageAPI
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

    /*
    private val weaponCache: MutableMap<Int,Bitmap> = mutableMapOf()
    private val bulletCache: MutableMap<Int,Bitmap> = mutableMapOf()
    private val medikitCache: MutableMap<Int,Bitmap> = mutableMapOf()
    private val upgradeCache: MutableMap<Int,Bitmap> = mutableMapOf()
    private val playerCache: MutableMap<Int,Bitmap> = mutableMapOf()

     */
    private val weaponCache: MutableMap<Int,MutableStateFlow<ByteArray>> = mutableMapOf()
    private val bulletCache: MutableMap<Int,MutableStateFlow<ByteArray>> = mutableMapOf()
    private val medikitCache: MutableMap<Int,MutableStateFlow<ByteArray>> = mutableMapOf()
    private val upgradeCache: MutableMap<Int,MutableStateFlow<ByteArray>> = mutableMapOf()
    private val playerCache: MutableMap<Int,MutableStateFlow<ByteArray>> = mutableMapOf()
    private val banditCache: MutableMap<Int, MutableStateFlow<ByteArray>> = mutableMapOf()
    private val mercenaryCache: MutableMap<Int, MutableStateFlow<ByteArray>> = mutableMapOf()

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

    fun getBulletFlow(id: Int): MutableStateFlow<ByteArray> {
        if(!bulletCache.containsKey(id)) {
            bulletCache[id] = MutableStateFlow(notFound)
            localScope.launch{
                Log.i(TAG, "[ImageLoader] Bullet $id")
                val response = getBulletImageAPI(id)
                if(response != null){
                    val bytes = Base64.decode(response.image, DEFAULT)
                    bulletCache[id]!!.value = bytes
                }
            }
        }
        return bulletCache[id]!!
    }

    fun getMedikitFlow(id: Int): MutableStateFlow<ByteArray> {
        if(!medikitCache.containsKey(id)) {
            medikitCache[id] = MutableStateFlow(notFound)
            localScope.launch{
                Log.i(TAG, "[ImageLoader] Medikit $id")
                val response = getMedikitImageAPI(id)
                if(response != null){
                    val bytes = Base64.decode(response.image, DEFAULT)
                    medikitCache[id]!!.value = bytes
                }
            }
        }
        return medikitCache[id]!!
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

    fun getMercenaryFlow(id: Int): MutableStateFlow<ByteArray>{
        if(!mercenaryCache.containsKey(id)) {
            mercenaryCache[id] = MutableStateFlow(notFound)
            localScope.launch{
                Log.i(TAG, "[ImageLoader] Mercenary $id")
                val response = getMercenaryImage(id)
                if(response != null){
                    val bytes = Base64.decode(response.image, DEFAULT)
                    val result = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
                    mercenaryCache[id]!!.value = bytes
                }
            }
        }
        return mercenaryCache[id]!!
    }

    fun invalidatePlayerImage(id: Int){
        playerCache.remove(id)
    }

    suspend fun loadFrom(repo: GameRepository){
        getPlayerFlow(repo.player.player.value.id)
        //shop bullets
        for(bullet in repo.shop.bullets.value){
            getBulletFlow(bullet.id)
        }
        //shop weapons
        for(w in repo.shop.weapons.value){
            getWeaponFlow(w.id)
        }
        //shop medikits
        for(m in repo.shop.medikits.value){
            getMedikitFlow(m.id)
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
}