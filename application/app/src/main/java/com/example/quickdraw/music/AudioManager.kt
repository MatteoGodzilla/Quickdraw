package com.example.quickdraw.music

import android.content.Context
import android.media.MediaPlayer
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.example.quickdraw.R

//https://pixabay.com/music/traditional-country-cowboy-western-background-247644/
object AudioManager: DefaultLifecycleObserver {
    private var mediaPlayer: MediaPlayer? = null

    fun init(context: Context,cycle: Lifecycle) {
        cycle.addObserver(this)
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.background_theme)
            mediaPlayer?.isLooping = true
        }
    }

    override fun onStart(owner: LifecycleOwner) {
        mediaPlayer?.start()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        mediaPlayer?.stop()
        mediaPlayer?.release()
    }

    override fun onStop(owner: LifecycleOwner) {
        mediaPlayer?.pause()
    }
}