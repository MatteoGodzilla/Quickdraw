package com.example.quickdraw.game.repo

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.example.quickdraw.PrefKeys
import com.example.quickdraw.network.api.getLevelsAPI
import com.example.quickdraw.network.api.getStatusAPI
import com.example.quickdraw.network.data.PlayerStatus
import com.example.quickdraw.runIfAuthenticated
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class PlayerRepository(
    private val dataStore: DataStore<Preferences>
) {
    //Status
    var status: MutableStateFlow<PlayerStatus?> = MutableStateFlow(null)
        private set
    var levels: List<Int> = listOf()
        private set
    var level: MutableStateFlow<Int> = MutableStateFlow(-1)
        private set

    suspend fun firstLoad(){
        getStatus()
        getLevels()
    }

    suspend fun getStatus() = runIfAuthenticated (dataStore){ auth ->
        val response = getStatusAPI(auth)
        if(response != null){
            status.update { response }
            runBlocking { dataStore.edit { it[PrefKeys.username] = response.username } }
            val lvl = getPlayerLevel()
            level.update { lvl }
            runBlocking { dataStore.edit { it[PrefKeys.level] = lvl.toString() } }
        }
    }

    suspend fun getLevels() = withContext(Dispatchers.IO) {
        levels = getLevelsAPI()
        val lvl = getPlayerLevel()
        level.update { lvl }
        runBlocking { dataStore.edit { it[PrefKeys.level] = lvl.toString() } }
    }

    private fun getPlayerLevel(): Int {
        if (status.value == null || levels.isEmpty()) {
            return -2;
        }
        var level = -1
        for (i in levels.indices) {
            if (status.value!!.exp >= levels[i]) {
                level = i + 1
            }
        }
        return level
    }
}