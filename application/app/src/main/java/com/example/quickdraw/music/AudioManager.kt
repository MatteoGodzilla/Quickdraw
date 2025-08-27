package com.example.quickdraw.music

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.example.quickdraw.R
import com.example.quickdraw.TAG

//https://pixabay.com/music/traditibgmonal-country-cowboy-western-background-247644/
//https://pixabay.com/music/rock-western-rock-252363/
//https://pixabay.com/sound-effects/gunshot-372470/
//https://pixabay.com/music/modern-country-western-165285/
enum class SONGS{
    BACKGROUND_1,
    BACKGROUND_2,
    BACKGROUND_3,
    LOBBY_THEME
}

object AudioManager: DefaultLifecycleObserver {

    private var mappedSongs = mapOf<Int,Int>(
        Pair(SONGS.BACKGROUND_1.ordinal,R.raw.background_theme),
        Pair(SONGS.BACKGROUND_2.ordinal,R.raw.background_theme2),
        Pair(SONGS.BACKGROUND_3.ordinal,R.raw.background_theme3),
    )

    private var bgm: MediaPlayer? = null
    private var sfx: MediaPlayer? = null

    fun init(context: Context,cycle: Lifecycle, initialVolume: Float) {
        cycle.addObserver(this)
        if (bgm == null) {
            bgm = MediaPlayer.create(context, R.raw.background_theme)
            bgm!!.isLooping = true
            bgm!!.setVolume(initialVolume, initialVolume)
        }
        if(sfx == null){
           sfx = MediaPlayer.create(context, R.raw.gunshot)
           sfx!!.isLooping = false
        }
    }

    fun changeBgmTheme(context: Context, choice:Int){
        if (mappedSongs.containsKey(choice) && mappedSongs[choice]!=null){
            bgm = MediaPlayer.create(context,mappedSongs[choice]!!)
            bgm!!.isLooping = true
        }
    }

    fun changeBgmTheme(context: Context, choice:SONGS){
        changeBgmTheme(context,choice.ordinal)
    }

    fun startSFX(){
        sfx?.start()
    }

    //Set volume

    fun setMusicVolume(vol: Float){
        bgm?.setVolume(vol, vol)
    }

    fun setSFXVolume(vol: Float){
        sfx?.setVolume(vol, vol)
    }

    //Lifecycle observer

    override fun onStart(owner: LifecycleOwner) {
        Log.i(TAG, "Started bgm $bgm")
        bgm?.start()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        bgm?.stop()
        bgm?.release()
    }

    override fun onStop(owner: LifecycleOwner) {
        bgm?.pause()
    }
}