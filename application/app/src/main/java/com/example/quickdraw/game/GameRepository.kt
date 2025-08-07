package com.example.quickdraw.game

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.quickdraw.PrefKeys
import com.example.quickdraw.TAG
import com.example.quickdraw.network.api.employMercenaryAPI
import com.example.quickdraw.network.api.getActiveContractsAPI
import com.example.quickdraw.network.api.getAllPlayerMercenariesAPI
import com.example.quickdraw.network.api.getAvailableContractsAPI
import com.example.quickdraw.network.api.getFriendLeaderboardAPI
import com.example.quickdraw.network.api.getGlobalLeaderboardAPI
import com.example.quickdraw.network.api.getHirableAPI
import com.example.quickdraw.network.api.getInventoryAPI
import com.example.quickdraw.network.api.getLevelsAPI
import com.example.quickdraw.network.api.getNextUnlockableMercenariesAPI
import com.example.quickdraw.network.api.getShopBulletsAPI
import com.example.quickdraw.network.api.getShopMedikitsAPI
import com.example.quickdraw.network.api.getShopUpgradesAPI
import com.example.quickdraw.network.api.getShopWeaponsAPI
import com.example.quickdraw.network.api.getStatusAPI
import com.example.quickdraw.network.api.getUnassignedMercenariesAPI
import com.example.quickdraw.network.api.redeemContractAPI
import com.example.quickdraw.network.api.startContractAPI
import com.example.quickdraw.network.data.ActiveContract
import com.example.quickdraw.network.data.AvailableContract
import com.example.quickdraw.network.data.EmployedMercenary
import com.example.quickdraw.network.data.HireableMercenary
import com.example.quickdraw.network.data.InventoryBullet
import com.example.quickdraw.network.data.InventoryMedikit
import com.example.quickdraw.network.data.InventoryUpgrade
import com.example.quickdraw.network.data.InventoryWeapon
import com.example.quickdraw.network.data.LeaderboardEntry
import com.example.quickdraw.network.data.LockedMercenary
import com.example.quickdraw.network.data.PlayerStatus
import com.example.quickdraw.network.data.ShopBullet
import com.example.quickdraw.network.data.ShopMedikit
import com.example.quickdraw.network.data.ShopUpgrade
import com.example.quickdraw.network.data.ShopWeapon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

class GameRepository(
    private val dataStore: DataStore<Preferences>
) {
    //TODO: change this to have MutableStateFlow<T> instead, so that ui can react and update automatically.
    //Because of coroutines, there is no guarantee that the ui has the data when it needs it
    //If it's a list, it should be non-null with an empty constructor
    //If it's a single object, it should be nullable with default as null

    //Status
    var player: MutableStateFlow<PlayerStatus?> = MutableStateFlow<PlayerStatus?>(null)
        private set
    var levels: List<Int> = listOf()
        private set
    var playerLevel: MutableStateFlow<Int> = MutableStateFlow(-1)
        private set
    //Inventory
    var bullets: MutableStateFlow<List<InventoryBullet>> = MutableStateFlow<List<InventoryBullet>>(listOf())
        private set
    var weapons: MutableStateFlow<List<InventoryWeapon>> = MutableStateFlow<List<InventoryWeapon>>(listOf())
        private set
    var medikits: MutableStateFlow<List<InventoryMedikit>> = MutableStateFlow<List<InventoryMedikit>>(listOf())
        private set
    var upgrades: MutableStateFlow<List<InventoryUpgrade>> = MutableStateFlow<List<InventoryUpgrade>>(listOf())
        private set
    //Contracts
    var activeContracts: MutableStateFlow<List<ActiveContract>> = MutableStateFlow<List<ActiveContract>>(listOf())
        private set
    var availableContracts: MutableStateFlow<List<AvailableContract>> = MutableStateFlow<List<AvailableContract>>(listOf())
        private set
    //Shop
    var shopWeapons: List<ShopWeapon> = listOf()
        private set
    var shopBullets: List<ShopBullet> = listOf()
        private set
    var shopMedikits: List<ShopMedikit> = listOf()
        private set
    var shopUpgrades: List<ShopUpgrade> = listOf()
        private set
    //Bounty board
    var friendLeaderboard: List<LeaderboardEntry> = listOf()
        private set
    var globalLeaderboard: List<LeaderboardEntry> = listOf()
        private set

    //Mercenaries
    var hireableMercenaries: MutableStateFlow<List<HireableMercenary>> =  MutableStateFlow(listOf())
        private set
    var nextUnlockablesMercenaries: MutableStateFlow<List<LockedMercenary>> =  MutableStateFlow(listOf())
        private set
    var playerEmployedMercenaries: MutableStateFlow<List<EmployedMercenary>> =  MutableStateFlow(listOf())
        private set
    var unAssignedMercenaries:MutableStateFlow<List<EmployedMercenary>> = MutableStateFlow(listOf())
        private set



    suspend fun getStatus() = runIfAuthenticated { auth ->
        val response = getStatusAPI(auth)
        player.update { x->response }
        playerLevel.update { x-> getPlayerLevel()}
    }

    suspend fun getLevels() = withContext(Dispatchers.IO) {
        levels = getLevelsAPI()
        playerLevel.value = getPlayerLevel()
    }

    private fun getPlayerLevel(): Int {
        if(player.value == null || levels.isEmpty()) {
            return -2;
        }
        var level = -1
        for(i in levels.indices){
            if(player.value!!.exp >= levels[i]){
                level = i + 1
            }
        }
        return level
    }

    suspend fun getInventory() = runIfAuthenticated { auth ->
        val response = getInventoryAPI(auth)
        if(response != null) {
            //Separate values
            bullets.update { x->response.bullets }
            weapons.update { x->response.weapons }
            medikits.update { x->response.medikits }
            upgrades.update { x->response.upgrades }
        }
    }

    suspend fun getContracts() = runIfAuthenticated{ auth ->
        activeContracts.update{x->getActiveContractsAPI(auth)}
        availableContracts.update{getAvailableContractsAPI(auth)}
    }

    suspend fun startContract(contract: AvailableContract,mercenaries:List<Int>) = runIfAuthenticated { auth ->
        val success = startContractAPI(auth, contract, mercenaries)
        //it should always be successful, otherwise there is a problem with the flow not being correct
        if(success){
            availableContracts.update { contracts->contracts.filter { ac -> contract.id != ac.id } }
            unAssignedMercenaries.update { x->x.filter { y->!mercenaries.any{z->z==y.idEmployment} } } // yeah i have to make this more readable i know
        }
    }

    suspend fun redeemContract(contract: ActiveContract) = runIfAuthenticated { auth->
        val success = redeemContractAPI(auth, contract)
        if(success){
           activeContracts.update{contracts->contracts.filter { c -> c.activeId != contract.activeId }}
        }
    }

    suspend fun getShopWeapons() = runIfAuthenticated { auth ->
        shopWeapons = getShopWeaponsAPI(auth)
    }

    suspend fun getShopBullets() = runIfAuthenticated { auth ->
        shopBullets = getShopBulletsAPI(auth)
    }

    suspend fun getShopMedikits() = runIfAuthenticated { auth ->
        shopMedikits = getShopMedikitsAPI(auth)
    }

    suspend fun getShopUpgrades() = runIfAuthenticated { auth ->
        shopUpgrades = getShopUpgradesAPI(auth)
    }

    suspend fun getFriendLeaderboard() = runIfAuthenticated { auth ->
        friendLeaderboard = getFriendLeaderboardAPI(auth)
    }

    suspend fun getGlobalLeaderboard() = withContext(Dispatchers.IO) {
        globalLeaderboard = getGlobalLeaderboardAPI()
    }

    suspend fun getHireableMercenaries() = runIfAuthenticated { auth ->
        val response = getHirableAPI(auth)
        hireableMercenaries.update { x -> response.mercenaries }
    }

    suspend fun getPlayerEmployedMercenaries() = runIfAuthenticated { auth ->
        val response = getAllPlayerMercenariesAPI(auth)
        playerEmployedMercenaries.update { x -> response.mercenaries }
    }

    suspend fun getNextToUnlockMercenaries() = runIfAuthenticated { auth ->
        val response = getNextUnlockableMercenariesAPI(auth)
        nextUnlockablesMercenaries.update { x -> response.mercenaries }
    }

    suspend fun getUnassignedMercenaries() = runIfAuthenticated { auth ->
        val response = getUnassignedMercenariesAPI(auth)
        unAssignedMercenaries.update { x->response.mercenaries }
    }

    suspend fun employMercenary(mercenary: HireableMercenary) = runIfAuthenticated { auth ->
        val response = employMercenaryAPI(auth, mercenary = mercenary)
        if(response.idEmployment!=-1){
            hireableMercenaries.update{x->hireableMercenaries.value.filter { merc -> merc.id != mercenary.id }}
            val newEmploy = EmployedMercenary(response.idEmployment,
                mercenary.name,
                mercenary.power)
            playerEmployedMercenaries.update{x->x+newEmploy}
            unAssignedMercenaries.update {x->x+newEmploy}
        }
    }

    private suspend fun runIfAuthenticated(block: (authToken: String)->Unit) = withContext(Dispatchers.IO) {
        val authToken = dataStore.data.map { pref -> pref[PrefKeys.authToken] }.firstOrNull()
        if(authToken != null){
            block(authToken)
        } else {
            Log.e(TAG, "There was a problem retrieving the authToken")
        }
    }

}