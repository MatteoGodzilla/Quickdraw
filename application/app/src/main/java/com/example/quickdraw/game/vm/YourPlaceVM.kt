package com.example.quickdraw.game.vm

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableFloatStateOf
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickdraw.DEFAULT_VOLUME
import com.example.quickdraw.ImageLoader
import com.example.quickdraw.PrefKeys
import com.example.quickdraw.TAG
import com.example.quickdraw.dataStore
import com.example.quickdraw.game.repo.GameRepository
import com.example.quickdraw.login.LoginActivity
import com.example.quickdraw.music.AudioManager
import com.example.quickdraw.music.AudioManagerLifecycleObserver
import com.example.quickdraw.network.api.updateProfilePicAPI
import com.example.quickdraw.network.api.useMedikitAPI
import com.example.quickdraw.runIfAuthenticated
import com.example.quickdraw.signOff
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
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

    val favouriteWeapon = MutableStateFlow(-1)
    val favouriteSong = MutableStateFlow(0)

    val player = repository.player
    val stats = repository.player.stats
    val otherStatistics = repository.statistics
    var playerImage: MutableStateFlow<ByteArray> = imageLoader.getPlayerFlow(repository.player.player.value.id)

    val muteAudio = MutableStateFlow(false)
    val musicVolumeSlider = mutableFloatStateOf(DEFAULT_VOLUME)
    val sfxVolumeSlider = mutableFloatStateOf(DEFAULT_VOLUME)

    init{
        viewModelScope.launch {
            musicVolumeSlider.floatValue = context.dataStore.data.map { pref -> pref[PrefKeys.musicVolume] }.first() ?: DEFAULT_VOLUME
            sfxVolumeSlider.floatValue = context.dataStore.data.map { pref -> pref[PrefKeys.sfxVolume] }.first() ?: DEFAULT_VOLUME
            favouriteWeapon.value = context.dataStore.data.map { pref -> pref[PrefKeys.favouriteWeapon] }.first() ?: -1
            favouriteSong.value = context.dataStore.data.map { pref -> pref[PrefKeys.favouriteTheme] }.first() ?: 0
            muteAudio.value = context.dataStore.data.map { pref -> pref[PrefKeys.musicMute] }.first() ?: false
        }
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
                    //update for composition
                    playerImage.update {
                       bytes
                    }
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

    fun useMedikit(id: Int) = viewModelScope.launch{
        runIfAuthenticated(context.dataStore) { authToken ->
            val res = useMedikitAPI(authToken, id)
            if(res != null){
                repository.player.player.value = repository.player.player.value.copy(health = res.newHealth)
                val decreasingMedikits = repository.inventory.medikits.value.map { m ->
                    if(m.id == id) m.copy(amount = res.amountLeft) else m
                }
                repository.inventory.medikits.value = decreasingMedikits.filter{ m -> m.amount > 0 }
            }
        }
    }

    fun setMusicVolume(value: Float) = viewModelScope.launch {
        context.dataStore.edit { pref->pref[PrefKeys.musicVolume] = value }
        musicVolumeSlider.floatValue = value
        AudioManager.setBGMVolume(value)
    }
    fun setSFXVolume(value: Float) = viewModelScope.launch {
        context.dataStore.edit { pref->pref[PrefKeys.sfxVolume] = value }
        sfxVolumeSlider.floatValue = value
        AudioManager.setSFXVolume(value)
    }

    fun loadWeaponImage(id:Int) = imageLoader.getWeaponFlow(id)
    fun loadBulletImage(id: Int) = imageLoader.getBulletFlow(id)
    fun loadMedikitImage(id: Int) = imageLoader.getMedikitFlow(id)
    fun loadUpgradeImage(id: Int) = imageLoader.getUpgradeFlow(id)

    fun setOrUnsetFavourite(id:Int){
        if(favouriteWeapon.value!=id){
            favouriteWeapon.update { id }
            viewModelScope.launch {
                context.dataStore.edit { preferences ->
                    preferences[PrefKeys.favouriteWeapon] = id
                }
            }
        }
        else{
            favouriteWeapon.update { -1 }
            viewModelScope.launch {
                context.dataStore.edit { pref->pref.remove(PrefKeys.favouriteWeapon)}
            }
        }
    }

    fun changeAudioManagerSong(choice:Int){
        viewModelScope.launch {
            favouriteSong.update { choice }
            context.dataStore.edit { pref->pref[PrefKeys.favouriteTheme] = choice }
            AudioManager.changeBgmTheme(context,choice,musicVolumeSlider.floatValue)
        }
    }

    fun onMuteToggle(selected: Boolean) = viewModelScope.launch{
        muteAudio.value = selected
        context.dataStore.edit { pref -> pref[PrefKeys.musicMute] = selected }
        if(selected){
            AudioManager.pauseBGM()
            AudioManagerLifecycleObserver.detach()
        } else {
            AudioManager.playBGM()
            AudioManagerLifecycleObserver.attach()
        }
    }

}