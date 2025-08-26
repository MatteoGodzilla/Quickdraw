package com.example.quickdraw.game.repo

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.quickdraw.network.api.getActiveContractsAPI
import com.example.quickdraw.network.api.getAvailableContractsAPI
import com.example.quickdraw.network.api.getContractStatsAPI
import com.example.quickdraw.network.api.redeemContractAPI
import com.example.quickdraw.network.api.startContractAPI
import com.example.quickdraw.network.data.ActiveContract
import com.example.quickdraw.network.data.AvailableContract
import com.example.quickdraw.network.data.ContractStats
import com.example.quickdraw.runIfAuthenticated
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class ContractsRepository (
    private val dataStore: DataStore<Preferences>,
    private val mercenaryRepo: MercenaryRepository,
    private val playerRepository: PlayerRepository
){
    var active: MutableStateFlow<List<ActiveContract>> = MutableStateFlow(listOf())
        private set
    var available: MutableStateFlow<List<AvailableContract>> = MutableStateFlow(listOf())
        private set
    var lastRedeemed = MutableStateFlow(0)

    suspend fun getContracts() = runIfAuthenticated( dataStore ) { auth ->
        active.update { getActiveContractsAPI(auth) }
        available.update { getAvailableContractsAPI(auth) }
    }

    suspend fun start(contract: AvailableContract, mercenaries: List<Int>) =
        runIfAuthenticated( dataStore ) { auth ->
            val response = startContractAPI(auth, contract, mercenaries)
            //it should always be successful, otherwise there is a problem with the flow not being correct
            if (response.success) {
                val info = response.contractInfo
                playerRepository.player.update { x ->
                    x.copy(money = x.money - contract.startCost)
                    /*
                    PlayerStatus(
                        x.id,
                        x.health,
                        x.maxHealth,
                        x.exp,
                        x.money - contract.startCost,
                        x.bounty,
                        x.username
                    )
                     */
                }
                active.update { list ->
                    list + ActiveContract(
                        info.idActiveContract, contract.name, contract.requiredTime, info.startTime,
                        mercenaries = mercenaryRepo.unAssigned.value.filter { m ->
                            mercenaries.any { y -> y == m.idEmployment }
                        })
                }
                available.update { it.filter { ac -> contract.id != ac.id } }
                mercenaryRepo.unAssigned.update { it.filter { y -> !mercenaries.any { z -> z == y.idEmployment } } } // yeah i have to make this more readable i know
            }
        }

    suspend fun redeem(contract: ActiveContract) = runIfAuthenticated( dataStore ) { auth ->
        val response = redeemContractAPI(auth, contract)
        if (response.success) {
            active.update { it.filter { c -> c.activeId != contract.activeId } }
            playerRepository.player.update { x ->
                x.copy(money = x.money + response.reward)
            }
            lastRedeemed.update { response.reward }
            available.update { it + response.returnableContract }
            mercenaryRepo.unAssigned.update { it + contract.mercenaries }
        }
    }
}