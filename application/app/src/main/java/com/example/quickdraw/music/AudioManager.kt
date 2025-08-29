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

data class Song(val name:String, val resource:Int)

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

object AudioManager {

    private var mappedSongs = mapOf(
        Pair(SONGS.BACKGROUND_1.ordinal,Song("Theme 1",R.raw.background_theme)),
        Pair(SONGS.BACKGROUND_2.ordinal,Song("Theme 2",R.raw.background_theme2)),
        Pair(SONGS.BACKGROUND_3.ordinal,Song("Theme 3",R.raw.background_theme3)),
    )

    private var bgm: MediaPlayer? = null
    private var sfx: MediaPlayer? = null

    private var currTheme = SONGS.BACKGROUND_1.ordinal


    fun init(context: Context, initialVolume: Float) {
        if (bgm == null) {
            bgm = MediaPlayer.create(context, mappedSongs[currTheme]!!.resource)
            bgm!!.isLooping = true
            bgm!!.setVolume(initialVolume, initialVolume)
        }
        if(sfx == null){
           sfx = MediaPlayer.create(context, R.raw.gunshot)
           sfx!!.isLooping = false
        }
    }

    fun getSettingsSongs() : List<Song>{
        return mappedSongs.map{x->x.value}
    }

    fun setTheme(choice:Int){
        if (mappedSongs.containsKey(choice) && mappedSongs[choice]!=null)
            currTheme = choice
    }

    fun changeBgmTheme(context: Context, choice:Int,volume:Float){
        Log.i(TAG,"Changing song")
        if (mappedSongs.containsKey(choice) && mappedSongs[choice]!=null){
            currTheme = choice
            replaySong(context,volume)
            Log.i(TAG,"Song changed")
        }
    }

    private fun replaySong(context:Context,volume:Float){
        stopBGM()
        Log.i(TAG,"Volume:${volume.toString()}")
        bgm = MediaPlayer.create(context,mappedSongs[currTheme]!!.resource)
        bgm?.isLooping = true
        bgm?.setVolume(volume,volume)
        playBGM()
    }

    fun playBGM(){
        bgm?.start()
    }

    fun pauseBGM() {
        bgm?.pause()
    }

    fun stopBGM(){
        bgm?.stop()
        bgm?.release()
        bgm = null
    }

    fun startSFX(){
        sfx?.start()
    }

    //Set volume

    fun setBGMVolume(vol: Float){
        bgm?.setVolume(vol, vol)
    }

    fun setSFXVolume(vol: Float){
        sfx?.setVolume(vol, vol)
    }

}