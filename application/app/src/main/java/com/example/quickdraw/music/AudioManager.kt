package com.example.quickdraw.music

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import com.example.quickdraw.R
import com.example.quickdraw.TAG

//https://pixabay.com/music/traditibgmonal-country-cowboy-western-background-247644/
//https://pixabay.com/music/rock-western-rock-252363/
//https://pixabay.com/sound-effects/gunshot-372470/
//https://pixabay.com/music/modern-country-western-165285/
//Paper Mario The Thousand Year Door, Stylish sound effect
//https://www.youtube.com/watch?v=B2hlU_O2Mjo
enum class SONGS{
    BACKGROUND_1,
    BACKGROUND_2,
    BACKGROUND_3,
    LOBBY_THEME
}

enum class SFX{
    BOOM,
    STYLISH,
    SHOP_PURCHASE
}

data class Song(val name:String, val resource:Int)


object AudioManager {

    private var mappedSongs = mapOf(
        SONGS.BACKGROUND_1.ordinal to Song("Theme 1",R.raw.background_theme),
        SONGS.BACKGROUND_2.ordinal to Song("Theme 2",R.raw.background_theme2),
        SONGS.BACKGROUND_3.ordinal to Song("Theme 3",R.raw.background_theme3),
    )


    private var bgm: MediaPlayer? = null

    private var currTheme = SONGS.BACKGROUND_1.ordinal

    private var mappedSFX = mapOf(
        SFX.BOOM to R.raw.gunshot,
        SFX.STYLISH to R.raw.stylish_ttyd,
        SFX.SHOP_PURCHASE to R.raw.shop_purchase
    )
    private var mappedSFXMediaPlayer = mutableMapOf<SFX, MediaPlayer>()

    fun init(context: Context, initialBGMVolume: Float, initialSFXVolume: Float) {
        if (bgm == null) {
            bgm = MediaPlayer.create(context, mappedSongs[currTheme]!!.resource)
            bgm!!.isLooping = true
            bgm!!.setVolume(initialBGMVolume, initialBGMVolume)
        }
        for (pair in mappedSFX){
            val mediaPlayer = MediaPlayer.create(context, mappedSFX[pair.key]!!)
            mediaPlayer.isLooping = false
            mediaPlayer.setVolume(initialSFXVolume, initialSFXVolume)
            mappedSFXMediaPlayer[pair.key] = mediaPlayer
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

    fun startSFX(type: SFX){
        Log.i(TAG, "STARTED SFX $type")
        mappedSFXMediaPlayer[type]?.also {
            if(it.isPlaying){
                it.pause()
                it.seekTo(0)
            }
            it.start()
        }
    }

    //Set volume

    fun setBGMVolume(vol: Float){
        bgm?.setVolume(vol, vol)
    }

    fun setSFXVolume(vol: Float){
        for (pair in mappedSFXMediaPlayer){
            mappedSFXMediaPlayer[pair.key]!!.setVolume(vol, vol)
        }
    }

}