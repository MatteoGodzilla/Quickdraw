package com.example.quickdraw

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Base64.DEFAULT
import android.util.Log
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import com.example.quickdraw.game.repo.GameRepository
import com.example.quickdraw.network.api.getBulletImageAPI
import com.example.quickdraw.network.api.getMedikitImageAPI
import com.example.quickdraw.network.api.getPlayerImageAPI
import com.example.quickdraw.network.api.getUpgradeImageAPI
import com.example.quickdraw.network.api.getWeaponImageAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImageLoader(context: Context) {
    var imageNotFound : Bitmap = AppCompatResources.getDrawable(context, R.drawable.question_mark_24px)!!.toBitmap(512, 512)

    private val localScope = CoroutineScope(Dispatchers.IO)

    /*
    private val weaponCache: MutableMap<Int,Bitmap> = mutableMapOf()
    private val bulletCache: MutableMap<Int,Bitmap> = mutableMapOf()
    private val medikitCache: MutableMap<Int,Bitmap> = mutableMapOf()
    private val upgradeCache: MutableMap<Int,Bitmap> = mutableMapOf()
    private val playerCache: MutableMap<Int,Bitmap> = mutableMapOf()

     */
    private val weaponCache: MutableMap<Int,MutableStateFlow<ImageBitmap>> = mutableMapOf()
    private val bulletCache: MutableMap<Int,MutableStateFlow<ImageBitmap>> = mutableMapOf()
    private val medikitCache: MutableMap<Int,MutableStateFlow<ImageBitmap>> = mutableMapOf()
    private val upgradeCache: MutableMap<Int,MutableStateFlow<ImageBitmap>> = mutableMapOf()
    private val playerCache: MutableMap<Int,MutableStateFlow<ImageBitmap>> = mutableMapOf()

    private val weaponLoading: MutableMap<Int,Boolean> = mutableMapOf()
    private val bulletLoading: MutableMap<Int,Boolean> = mutableMapOf()
    private val medikitLoading: MutableMap<Int,Boolean> = mutableMapOf()
    private val upgradeLoading: MutableMap<Int,Boolean> = mutableMapOf()
    private val playerLoading: MutableMap<Int,Boolean> = mutableMapOf()

    fun getWeaponFlow(id: Int): MutableStateFlow<ImageBitmap> {
        if(!weaponCache.containsKey(id)) {
            weaponCache[id] = MutableStateFlow(imageNotFound.asImageBitmap())
            localScope.launch{
                weaponLoading[id] = true
                Log.i(TAG, "[ImageLoader] Weapon $id")
                val response = getWeaponImageAPI(id)
                if(response != null){
                    val bytes = Base64.decode(response.image, DEFAULT)
                    val result = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
                    weaponCache[id]!!.value = result.asImageBitmap()
                }
                weaponLoading[id] = false
            }
        }
        return weaponCache[id]!!
    }

    fun getBulletFlow(id: Int): MutableStateFlow<ImageBitmap> {
        if(!bulletCache.containsKey(id)) {
            bulletCache[id] = MutableStateFlow(imageNotFound.asImageBitmap())
            localScope.launch{
                bulletLoading[id] = true
                Log.i(TAG, "[ImageLoader] Bullet $id")
                val response = getBulletImageAPI(id)
                if(response != null){
                    val bytes = Base64.decode(response.image, DEFAULT)
                    val result = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
                    bulletCache[id]!!.value = result.asImageBitmap()
                }
                bulletLoading[id] = false
            }
        }
        return bulletCache[id]!!
    }

    fun getMedikitFlow(id: Int): MutableStateFlow<ImageBitmap> {
        if(!medikitCache.containsKey(id)) {
            medikitCache[id] = MutableStateFlow(imageNotFound.asImageBitmap())
            localScope.launch{
                medikitLoading[id] = true
                Log.i(TAG, "[ImageLoader] Medikit $id")
                val response = getMedikitImageAPI(id)
                if(response != null){
                    val bytes = Base64.decode(response.image, DEFAULT)
                    val result = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
                    medikitCache[id]!!.value = result.asImageBitmap()
                }
                medikitLoading[id] = false
            }
        }
        return medikitCache[id]!!
    }

    fun getUpgradeFlow(id: Int): MutableStateFlow<ImageBitmap> {
        if(!upgradeCache.containsKey(id)) {
            upgradeCache[id] = MutableStateFlow(imageNotFound.asImageBitmap())
            localScope.launch{
                upgradeLoading[id] = true
                Log.i(TAG, "[ImageLoader] Upgrade $id")
                val response = getUpgradeImageAPI(id)
                if(response != null){
                    val bytes = Base64.decode(response.image, DEFAULT)
                    val result = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
                    upgradeCache[id]!!.value = result.asImageBitmap()
                }
                upgradeLoading[id] = false
            }
        }
        return upgradeCache[id]!!
    }

    fun getPlayerFlow(id: Int): MutableStateFlow<ImageBitmap> {
        if(!playerCache.containsKey(id)) {
            playerCache[id] = MutableStateFlow(imageNotFound.asImageBitmap())
            localScope.launch{
                playerLoading[id] = true
                Log.i(TAG, "[ImageLoader] Player $id")
                val response = getPlayerImageAPI(id)
                if(response != null){
                    val bytes = Base64.decode(response.image, DEFAULT)
                    val result = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
                    playerCache[id]!!.value = result.asImageBitmap()
                }
                playerLoading[id] = false
            }
        }
        return playerCache[id]!!
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
    }
}