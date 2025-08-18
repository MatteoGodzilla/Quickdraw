package com.example.quickdraw.game.repo

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.example.quickdraw.PrefKeys
import com.example.quickdraw.network.api.getBaseAPI
import com.example.quickdraw.network.api.getLevelsAPI
import com.example.quickdraw.network.api.getStatusAPI
import com.example.quickdraw.network.data.BaseStats
import com.example.quickdraw.network.data.PlayerInfo
import com.example.quickdraw.network.data.ShopUpgrade
import com.example.quickdraw.runIfAuthenticated
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

enum class Evaluation {
    INCREMENT, MULTIPLIER
}

data class Player(
    val id: Int,
    val health: Int,
    val exp: Int,
    val level: Int,
    val money: Int,
    val bounty: Int,
    val username: String
)

data class Stats(
    var maxHealth:Int,
    var expBoost:Int,
    var moneyBoost:Int,
    var bountyBoost:Int,
    var maxContracts: Int
)

class PlayerRepository(
    private val dataStore: DataStore<Preferences>
) {
    //Status
    //TODO: Merge status with level, as to have a single object with everything
    //(It doesn't make sense to have _just_ the level separated

    var player: MutableStateFlow<Player> = MutableStateFlow( Player(0, 0, 0, 0, 0, 0,"" ) )
        private set
    var stats: MutableStateFlow<Stats> = MutableStateFlow(Stats(50,100,100,100,1))
    private var status: PlayerInfo? = null
    private var baseStats: List<BaseStats> = listOf()
    private var statusStats: List<BaseStats> = listOf()
    private var levels: List<Int> = listOf()

    suspend fun firstLoad(){
        getStatus()
        getLevels()
        getBase()
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
        }
    }

    private suspend fun getLevels() = withContext(Dispatchers.IO) {
        levels = getLevelsAPI()
        updatePlayer()
    }

    private suspend fun getBase() = runIfAuthenticated(dataStore){ auth->
        val response = getBaseAPI()
        baseStats = response
        updateStats()
    }

    private fun updatePlayer(){
        var level = 0
        if(status != null){
            for (i in levels.indices) {
                if (status!!.exp >= levels[i]) {
                    level = i + 1
                }
            }
            player.value = Player(status!!.id, status!!.health, status!!.exp, level, status!!.money, status!!.bounty,status!!.username)
            runBlocking {
                dataStore.edit { it[PrefKeys.username] = status!!.username }
                dataStore.edit { it[PrefKeys.level] = level.toString() }
            }
        }
    }

    private fun updateStats(){
        statusStats = baseStats.toMutableList()
        for(upgrade in statusStats){
            when (upgrade.upgradeType) {
                UpgradeIds.MAX_HEALTH.ordinal -> {
                    stats.update { x->x.copy(maxHealth = upgrade.baseValue) }
                }
                UpgradeIds.MAX_CONTRACTS.ordinal -> {
                    stats.update { x->x.copy(maxContracts = upgrade.baseValue) }
                }
                UpgradeIds.MONEY_BOOST.ordinal -> {
                    stats.update { x->x.copy(moneyBoost = upgrade.baseValue) }
                }
                UpgradeIds.EXP_BOOST.ordinal -> {
                    stats.update { x->x.copy(expBoost = upgrade.baseValue) }
                }
                UpgradeIds.BOUNTY_BOOST.ordinal -> {
                    stats.update { x->x.copy(bountyBoost = upgrade.baseValue) }
                }
            }
        }
    }
}