package com.example.quickdraw.game.vm

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickdraw.ImageLoader
import com.example.quickdraw.TAG
import com.example.quickdraw.dataStore
import com.example.quickdraw.game.repo.GameRepository
import com.example.quickdraw.login.LoginActivity
import com.example.quickdraw.network.api.updateProfilePicAPI
import com.example.quickdraw.runIfAuthenticated
import com.example.quickdraw.signOff
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.io.encoding.Base64

class YourPlaceVM(
    private val repository: GameRepository,
    private val imageLoader: ImageLoader,
    private val context: ComponentActivity
) : ViewModel() {
    val weapons = repository.inventory.weapons
    val medikits = repository.inventory.medikits
    val upgrades = repository.inventory.upgrades
    val bullets = repository.inventory.bullets

    val player = repository.player.player
    val stats = repository.player.stats
    val playerImage: MutableStateFlow<ImageBitmap> = MutableStateFlow(imageLoader.imageNotFound.asImageBitmap())

    init{
        updatePlayerPic()
    }

    fun getProgressToNextLevel() = repository.player.getProgressToNextLevel()

    fun updateImage(uri: Uri) = viewModelScope.launch{
        Log.i(TAG, "Started uploading image")
        val input = context.contentResolver.openInputStream(uri)
        Log.i(TAG, input.toString())
        if(input != null){
            runIfAuthenticated(context.dataStore) { authToken ->
                Log.i(TAG, "Authenticated")
                val bytes = input.readBytes()
                val result = updateProfilePicAPI( authToken, Base64.encode(bytes) )
                if(result){
                    imageLoader.invalidatePlayerImage(repository.player.player.value.id)
                    updatePlayerPic()
                }
                input.close()
            }
        }
    }

    fun logout() = viewModelScope.launch{
        signOff(context.dataStore)
        val intent = Intent(context, LoginActivity::class.java)
        context.startActivity(intent)
        Log.i(TAG, "Sending from Register to Game activity")
    }

    private fun updatePlayerPic() = viewModelScope.launch {
        playerImage.value = imageLoader.getPlayerImage(repository.player.player.value.id)
    }

}