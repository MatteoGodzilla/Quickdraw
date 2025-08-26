package com.example.quickdraw.game.repo

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.quickdraw.network.api.getContractStatsAPI
import com.example.quickdraw.network.api.getRoundStatisticsAPI
import com.example.quickdraw.network.data.ContractStats
import com.example.quickdraw.network.data.RoundStatistics
import com.example.quickdraw.runIfAuthenticated
import kotlinx.coroutines.flow.MutableStateFlow

class StatisticsRepository(
    private val dataStore: DataStore<Preferences>
) {
    val contracts = MutableStateFlow(ContractStats(0,0,0))
    val rounds = MutableStateFlow(RoundStatistics(0,0,0,0,0,0))

    suspend fun firstLoad() {
        getRoundStats()
        getContractStats()
    }
    suspend fun getRoundStats() = runIfAuthenticated(dataStore) { authToken ->
        val response = getRoundStatisticsAPI(authToken)
        if(response != null){
            rounds.value = response
        }
    }

    suspend fun getContractStats() = runIfAuthenticated(dataStore) { authToken->
        val response = getContractStatsAPI(authToken)
        if(response != null){
            contracts.value = response
        }
    }

}