package com.example.quickdraw.ui.theme

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Base64.DEFAULT
import android.util.Log
import androidx.compose.ui.graphics.asImageBitmap
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.quickdraw.TAG
import com.example.quickdraw.network.api.getPlayerImageAPI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.collections.set

class ImageManager(val context: Context) {

    val imageLoader = ImageLoader.Builder(context)
        .memoryCache {
            MemoryCache.Builder()
                .maxSizePercent(context, 0.25)
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(context.cacheDir.resolve("image_cache"))
                .maxSizePercent(0.02)
                .build()
        }
        .build()

    private fun idPlayerCacheKey(id:Int):String{
        return "p_$id"
    }

    fun getPlayer(id:Int): ImageRequest{
        //check for cache contents first
        val key = MemoryCache.Key(idPlayerCacheKey(id))
        val cached = imageLoader.memoryCache!![key]
        if(cached==null){
            val base64Content = getPlayerImageAPI(id)
            if(base64Content != null){
                val bytes = Base64.decode(base64Content.image, DEFAULT)
                val request = ImageRequest.Builder(context)
                    .data(bytes)
                    .memoryCacheKey("player_$id")
                    .diskCacheKey("player_$id")
                    .build()
                return request
            }
        }
        return ImageRequest.Builder(context)
            .data(cached).build()
    }
}