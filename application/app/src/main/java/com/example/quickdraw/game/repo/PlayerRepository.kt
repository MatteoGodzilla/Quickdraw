package com.example.quickdraw.game.repo

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.quickdraw.network.api.getLevelsAPI
import com.example.quickdraw.network.api.getStatusAPI
import com.example.quickdraw.network.data.PlayerStatus
import com.example.quickdraw.runIfAuthenticated
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

//This class should not be accessed directly, but through GameRepository
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
        status.update { response }
        level.update { getPlayerLevel() }
    }

    suspend fun getLevels() = withContext(Dispatchers.IO) {
        levels = getLevelsAPI()
        level.value = getPlayerLevel()
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