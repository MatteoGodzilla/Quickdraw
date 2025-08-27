package com.example.quickdraw.game.repo

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.quickdraw.TAG
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class UpgradeIds{
    UNKOWN,
    MAX_HEALTH,
    MAX_CONTRACTS,
    MONEY_BOOST,
    BOUNTY_BOOST,
    EXP_BOOST
}

class GameRepository(
    dataStore: DataStore<Preferences>
) {
    val player = PlayerRepository(dataStore)
    val inventory = InventoryRepository(dataStore)
    val mercenaries = MercenaryRepository(dataStore, player)
    val contracts = ContractsRepository(dataStore, mercenaries, player)
    val leaderboard = LeaderboardRepository(dataStore)
    val shop = ShopRepository(dataStore, player, inventory)
    val bandits = BanditRepository(dataStore,player)
    val statistics = StatisticsRepository(dataStore)

    suspend fun firstLoad() {
        coroutineScope {
            //this is a test,to reverse back the commented function is below
            val playerThread = async{player.firstLoad()}
            val inventoryThread  = async{inventory.getInventory()}
            val contractsThread  = async{contracts.getContracts()}
            val shopThread  = async{shop.firstLoad()}
            val leaderboardThread  = async{leaderboard.firstLoad()}
            val mercenariesThread  = async{mercenaries.firstLoad()}
            val statsThread  = async{statistics.firstLoad()}
            awaitAll(playerThread,inventoryThread,contractsThread,shopThread,leaderboardThread,mercenariesThread,statsThread)
        }.also {
            updatePlayerStats()
        }
    }

    /**suspend fun firstLoad() {
        player.firstLoad()
        inventory.getInventory()
        contracts.getContracts()
        shop.firstLoad()
        leaderboard.firstLoad()
        mercenaries.firstLoad()
        statistics.firstLoad()
        updatePlayerStats()
    }**/

     fun updatePlayerStats(){
        for(upgrade in inventory.upgrades.value){
            when (upgrade.type) {
                UpgradeIds.MAX_HEALTH.ordinal -> {
                    player.stats.update { x->x.copy(maxHealth = x.maxHealth+upgrade.modifier) }
                }
                UpgradeIds.MAX_CONTRACTS.ordinal -> {
                    player.stats.update { x->x.copy(maxContracts = x.maxContracts+upgrade.modifier) }
                }
                UpgradeIds.MONEY_BOOST.ordinal -> {
                    player.stats.update { x->x.copy(moneyBoost = x.moneyBoost+upgrade.modifier) }
                }
                UpgradeIds.BOUNTY_BOOST.ordinal -> {
                    player.stats.update { x->x.copy(bountyBoost = x.bountyBoost+upgrade.modifier) }
                }
                UpgradeIds.EXP_BOOST.ordinal -> {
                    player.stats.update { x->x.copy(expBoost = x.expBoost+upgrade.modifier) }
                }
            }
        }
        Log.i(TAG,player.stats.value.toString())
    }
}
