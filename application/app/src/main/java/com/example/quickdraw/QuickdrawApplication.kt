package com.example.quickdraw

import android.app.Application
import com.example.quickdraw.duel.PeerFinder

//Used to store state between activities, which cannot be serialized into the datastore
class QuickdrawApplication : Application() {
    lateinit var peerFinderSingleton: PeerFinder
    lateinit var imageLoader: ImageLoader

    override fun onCreate() {
        super.onCreate()
        peerFinderSingleton = PeerFinder(applicationContext)
        imageLoader = ImageLoader(applicationContext)
    }
}