package com.example.quickdraw

import android.app.Application
import com.example.quickdraw.duel.PeerFinder
import com.example.quickdraw.game.repo.GameRepository

//Used to store state between activities, which cannot be serialized into the datastore
class QuickdrawApplication : Application() {
    lateinit var peerFinderSingleton: PeerFinder
    lateinit var imageLoader: ImageLoader

    lateinit var repository: GameRepository

    override fun onCreate() {
        super.onCreate()
        peerFinderSingleton = PeerFinder(applicationContext)
        imageLoader = ImageLoader(applicationContext)
    }
}