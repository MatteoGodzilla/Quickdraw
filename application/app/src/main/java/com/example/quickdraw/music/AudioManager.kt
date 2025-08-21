package com.example.quickdraw.music

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.example.quickdraw.R
import com.example.quickdraw.TAG

//https://pixabay.com/music/traditional-country-cowboy-western-background-247644/
object AudioManager: DefaultLifecycleObserver {
    private var bgm: MediaPlayer? = null

    fun init(context: Context,cycle: Lifecycle, initialVolume: Float) {
        cycle.addObserver(this)
        if (bgm == null) {
            bgm = MediaPlayer.create(context, R.raw.background_theme)
            bgm?.isLooping = true
            bgm?.setVolume(initialVolume, initialVolume)
        }
    }

    fun setMusicVolume(vol: Float){
        bgm?.setVolume(vol, vol)
    }

    fun setSFXVolume(vol: Float){
        //TODO
    }

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