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
import com.example.quickdraw.network.api.getPlayerImageAPI
import com.example.quickdraw.network.api.getWeaponImageAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ImageLoader(context: Context) {
    var imageNotFound : Bitmap = AppCompatResources.getDrawable(context, R.drawable.question_mark_24px)!!.toBitmap(128, 128)

    private val weaponCache: MutableMap<Int,Bitmap> = mutableMapOf()
    private val playerCache: MutableMap<Int,Bitmap> = mutableMapOf()

    suspend fun getWeaponImage(id: Int): ImageBitmap = withContext(Dispatchers.IO){
        if(weaponCache.containsKey(id)){
            return@withContext weaponCache[id]!!.asImageBitmap()
        }
        //attempt to get image from server
        val response = getWeaponImageAPI(id)
        if(response != null){
            val bytes = Base64.decode(response.image, DEFAULT)
            val result = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
            weaponCache[id] = result
            return@withContext result.asImageBitmap()
        }

        return@withContext imageNotFound.asImageBitmap()
    }

    suspend fun getPlayerImage(id: Int) : ImageBitmap = withContext(Dispatchers.IO){
        if(playerCache.containsKey(id)){
            return@withContext playerCache[id]!!.asImageBitmap()
        }
        //attempt to get image from server
        val response = getPlayerImageAPI(id)
        if(response != null){
            val bytes = Base64.decode(response.image, Base64.DEFAULT)
            val result = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
            playerCache[id] = result
            return@withContext result.asImageBitmap()
        }

        return@withContext imageNotFound.asImageBitmap()
    }
}