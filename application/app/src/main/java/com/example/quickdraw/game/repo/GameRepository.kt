package com.example.quickdraw.game.repo

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

class GameRepository(
    dataStore: DataStore<Preferences>
) {
    val player = PlayerRepository(dataStore)
    val inventory = InventoryRepository(dataStore)
    val mercenaries = MercenaryRepository(dataStore, player)
    val contracts = ContractsRepository(dataStore, mercenaries, player)
    val leaderboard = LeaderboardRepository(dataStore)
    val shop = ShopRepository(dataStore, player, inventory)
    val peer = PeerRepository()

    suspend fun firstLoad() {
        player.firstLoad()
        inventory.getInventory()
        contracts.getContracts()
        shop.firstLoad()
        leaderboard.firstLoad()
        mercenaries.firstLoad()
    }
}
