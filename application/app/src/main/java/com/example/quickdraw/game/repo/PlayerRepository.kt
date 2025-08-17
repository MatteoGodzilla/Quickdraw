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

data class Player(
    val id: Int,
    val health: Int,
    val maxHealth: Int,
    val exp: Int,
    val level: Int,
    val money: Int,
    val bounty: Int,
    val username: String
)

class PlayerRepository(
    private val dataStore: DataStore<Preferences>
) {
    //Status
    //TODO: Merge status with level, as to have a single object with everything
    //(It doesn't make sense to have _just_ the level separated

    var player: MutableStateFlow<Player> = MutableStateFlow( Player(0, 0, 100, 0, 0, 0, 0,"" ) )
        private set
    private var status: PlayerStatus? = null
    private var levels: List<Int> = listOf()

    suspend fun firstLoad(){
        getStatus()
        getLevels()
    }

    fun getProgressToNextLevel(): Float {
        //TODO: use levels for this
        var progress = 0f
        if(levels.isNotEmpty()){
            val playerLevel = player.value.level
            val levelIndex = playerLevel - 1
            if(levelIndex < levels.size){
                //playerLevel -1 is a valid index
                progress = (player.value.exp - levels[levelIndex]).toFloat() / (levels[levelIndex + 1] - levels[levelIndex])
            }
        }
        return progress
    }

    private suspend fun getStatus() = runIfAuthenticated (dataStore){ auth ->
        //NOTE: response does not include level
        val response = getStatusAPI(auth)
        if(response != null){
            status = response
            updatePlayer()
        }
    }

    private suspend fun getLevels() = withContext(Dispatchers.IO) {
        levels = getLevelsAPI()
        updatePlayer()
    }

    private fun updatePlayer(){
        var level = 0
        if(status != null){
            for (i in levels.indices) {
                if (status!!.exp >= levels[i]) {
                    level = i + 1
                }
            }
            player.value = Player(status!!.id, status!!.health, status!!.maxHealth, status!!.exp, level, status!!.money, status!!.bounty,status!!.username)
            runBlocking {
                dataStore.edit { it[PrefKeys.username] = status!!.username }
                dataStore.edit { it[PrefKeys.level] = level.toString() }
            }
        }
    }
}