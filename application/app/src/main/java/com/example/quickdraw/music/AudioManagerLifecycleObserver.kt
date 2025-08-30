package com.example.quickdraw.music

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.example.quickdraw.TAG

object AudioManagerLifecycleObserver: DefaultLifecycleObserver {
    //Lifecycle observer
    private var lifecycleRef: Lifecycle? = null
    fun init(cycle: Lifecycle){
        lifecycleRef = cycle
    }

    fun attach() {
        lifecycleRef?.addObserver(this)
    }

    fun detach(){
        lifecycleRef?.removeObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        Log.i(TAG, "Started bgm from Lifecycle Observer")
        AudioManager.playBGM()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        AudioManager.stopBGM()
    }

    override fun onStop(owner: LifecycleOwner) {
        AudioManager.pauseBGM()
    }
}
