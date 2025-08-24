package com.example.quickdraw.game.repo

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.quickdraw.TAG
import kotlinx.coroutines.flow.update

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

    suspend fun firstLoad() {
        player.firstLoad()
        inventory.getInventory()
        contracts.getContracts()
        shop.firstLoad()
        leaderboard.firstLoad()
        mercenaries.firstLoad()
        updatePlayerStats()
    }

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
