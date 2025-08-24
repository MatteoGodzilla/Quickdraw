package com.example.quickdraw.game.repo

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.quickdraw.TAG
import com.example.quickdraw.network.api.getBanditsAPI
import com.example.quickdraw.network.api.getInventoryAPI
import com.example.quickdraw.network.data.Bandit
import com.example.quickdraw.runIfAuthenticated
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class BanditRepository(
    private val dataStore: DataStore<Preferences>,
    private val playerRepository: PlayerRepository
) {
    val bandits: MutableStateFlow<List<Bandit>> = MutableStateFlow(listOf())

    suspend fun getBandits()= runIfAuthenticated(dataStore) { auth ->
        val response = getBanditsAPI(auth)
        Log.i(TAG,response.toString())
        bandits.update { response.map { x->x.stats } }
    }
}