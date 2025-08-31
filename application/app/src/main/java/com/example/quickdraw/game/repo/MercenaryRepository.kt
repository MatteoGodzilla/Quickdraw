package com.example.quickdraw.game.repo

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.quickdraw.network.api.employMercenaryAPI
import com.example.quickdraw.network.api.getAllPlayerMercenariesAPI
import com.example.quickdraw.network.api.getHirableAPI
import com.example.quickdraw.network.api.getNextUnlockableMercenariesAPI
import com.example.quickdraw.network.api.getUnassignedMercenariesAPI
import com.example.quickdraw.network.data.EmployedMercenary
import com.example.quickdraw.network.data.HireableMercenary
import com.example.quickdraw.network.data.LockedMercenary
import com.example.quickdraw.runIfAuthenticated
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class MercenaryRepository (
    private val dataStore: DataStore<Preferences>,
    private val playerRepository: PlayerRepository
){
    var hireable: MutableStateFlow<List<HireableMercenary>> = MutableStateFlow(listOf())
        private set
    var nextUnlockables: MutableStateFlow<List<LockedMercenary>> =
        MutableStateFlow(listOf())
        private set
    var playerEmployed: MutableStateFlow<List<EmployedMercenary>> =
        MutableStateFlow(listOf())
        private set
    var unAssigned: MutableStateFlow<List<EmployedMercenary>> =
        MutableStateFlow(listOf())
        private set

    suspend fun firstLoad() {
        getHireableMercenaries()
        getNextToUnlockMercenaries()
        getPlayerEmployedMercenaries()
        getUnassignedMercenaries()
    }

    suspend fun getHireableMercenaries() = runIfAuthenticated(dataStore) { auth ->
        val response = getHirableAPI(auth)
        hireable.update { response.mercenaries }
    }

    suspend fun getPlayerEmployedMercenaries() = runIfAuthenticated(dataStore) { auth ->
        val response = getAllPlayerMercenariesAPI(auth)
        playerEmployed.update { response.mercenaries }
        sortEmployed()
    }

    suspend fun getNextToUnlockMercenaries() = runIfAuthenticated(dataStore) { auth ->
        val response = getNextUnlockableMercenariesAPI(auth)
        nextUnlockables.update { response.mercenaries }
    }

    suspend fun getUnassignedMercenaries() = runIfAuthenticated(dataStore) { auth ->
        val response = getUnassignedMercenariesAPI(auth)
        unAssigned.update { response.mercenaries }
    }

    suspend fun employ(mercenary: HireableMercenary) = runIfAuthenticated(dataStore) { auth ->
        val response = employMercenaryAPI(auth, mercenary = mercenary)
        if (response.idEmployment != -1) {
            //mercenary update data
            hireable.update { hireable.value.filter { merc -> merc.id != mercenary.id } }
            val newEmploy =
                EmployedMercenary(response.idEmployment, mercenary.id,mercenary.name, mercenary.power)
            playerEmployed.update { it + newEmploy }
            unAssigned.update { it + newEmploy }
            //player balance update data
            playerRepository.player.update { p -> p.copy(money = p.money - mercenary.cost) }
            sortEmployed()
        }
    }

    private fun sortEmployed(){
        playerEmployed.update { x->x.sortedBy { x->x.power } }
    }
}