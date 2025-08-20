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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class ImageLoader(context: Context) {
    var imageNotFound : Bitmap = AppCompatResources.getDrawable(context, R.drawable.question_mark_24px)!!.toBitmap(512, 512)

    private val weaponCache: MutableMap<Int,Bitmap> = mutableMapOf()
    private val bulletCache: MutableMap<Int,Bitmap> = mutableMapOf()
    private val medikitCache: MutableMap<Int,Bitmap> = mutableMapOf()
    private val upgradeCache: MutableMap<Int,Bitmap> = mutableMapOf()
    private val playerCache: MutableMap<Int,Bitmap> = mutableMapOf()

    private val weaponLoading: MutableMap<Int,Boolean> = mutableMapOf()
    private val bulletLoading: MutableMap<Int,Boolean> = mutableMapOf()
    private val medikitLoading: MutableMap<Int,Boolean> = mutableMapOf()
    private val upgradeLoading: MutableMap<Int,Boolean> = mutableMapOf()
    private val playerLoading: MutableMap<Int,Boolean> = mutableMapOf()


    suspend fun getWeaponBitmap(id: Int): ImageBitmap = withContext(Dispatchers.IO){
        while(weaponLoading.containsKey(id) && weaponLoading[id] ==true){
            delay(2)
            Log.i(TAG,"DELAYING")
        }

        if(weaponCache.containsKey(id)){
            return@withContext weaponCache[id]!!.asImageBitmap()
        }
        //attempt to get image from server
        weaponLoading[id] = true
        val response = getWeaponImageAPI(id)
        if(response != null){
            val bytes = Base64.decode(response.image, DEFAULT)
            val result = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
            weaponCache[id] = result
            weaponLoading[id] = false
            return@withContext result.asImageBitmap()
        }
        weaponLoading[id] = false
        return@withContext imageNotFound.asImageBitmap()
    }

    suspend fun getBulletBitmap(id: Int): ImageBitmap = withContext(Dispatchers.IO){

        if(bulletCache.containsKey(id)){
            return@withContext bulletCache[id]!!.asImageBitmap()
        }
        //attempt to get image from server
        bulletLoading[id] = true
        val response = getBulletImageAPI(id)
        if(response != null){
            val bytes = Base64.decode(response.image, DEFAULT)
            val result = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
            bulletCache[id] = result
            bulletLoading[id] = false
            return@withContext result.asImageBitmap()
        }
        bulletLoading[id] = false
        return@withContext imageNotFound.asImageBitmap()
    }

    suspend fun getMedikitBitmap(id: Int): ImageBitmap = withContext(Dispatchers.IO){

        if(medikitCache.containsKey(id)){
            return@withContext medikitCache[id]!!.asImageBitmap()
        }
        //attempt to get image from server
        medikitLoading[id] = true
        val response = getMedikitImageAPI(id)
        if(response != null){
            val bytes = Base64.decode(response.image, DEFAULT)
            val result = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
            medikitLoading[id] = false
            medikitCache[id] = result
            return@withContext result.asImageBitmap()
        }
        medikitLoading[id] = false
        return@withContext imageNotFound.asImageBitmap()
    }

    suspend fun getUpgradeBitmap(id: Int): ImageBitmap = withContext(Dispatchers.IO){
        if(upgradeCache.containsKey(id)){
            return@withContext upgradeCache[id]!!.asImageBitmap()
        }

        upgradeLoading[id] = true
        //attempt to get image from server
        val response = getUpgradeImageAPI(id)
        if(response != null){
            val bytes = Base64.decode(response.image, DEFAULT)
            val result = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
            upgradeCache[id] = result
            upgradeLoading[id] = false
            return@withContext result.asImageBitmap()
        }
        upgradeLoading[id] = false
        return@withContext imageNotFound.asImageBitmap()
    }

    suspend fun getPlayerImage(id: Int) : ImageBitmap = withContext(Dispatchers.IO){
        if(playerCache.containsKey(id)){
            return@withContext playerCache[id]!!.asImageBitmap()
        }
        //attempt to get image from server
        playerLoading[id] = true
        val response = getPlayerImageAPI(id)
        if(response != null){
            val bytes = Base64.decode(response.image, Base64.DEFAULT)
            val result = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
            playerCache[id] = result
            playerLoading[id] = false
            return@withContext result.asImageBitmap()
        }
        playerLoading[id] = false
        return@withContext imageNotFound.asImageBitmap()
    }

    fun invalidatePlayerImage(id: Int){
        playerCache.remove(id)
    }

    suspend fun loadFrom(repo: GameRepository){
        getPlayerImage(repo.player.player.value.id)
        //shop bullets
        for(bullet in repo.shop.bullets.value){
            getBulletBitmap(bullet.id)
        }
        //shop weapons
        for(w in repo.shop.weapons.value){
            getBulletBitmap(w.id)
        }
        //shop medikits
        for(m in repo.shop.medikits.value){
            getBulletBitmap(m.id)
        }
        //shop upgrades
        for(u in repo.shop.upgrades.value){
            getBulletBitmap(u.id)
        }
        //bounty leaderboard
        for(p in repo.leaderboard.global.value){
            getPlayerImage(p.id )
        }
    }
}