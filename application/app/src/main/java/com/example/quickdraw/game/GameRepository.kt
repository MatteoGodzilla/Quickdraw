package com.example.quickdraw.game

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.quickdraw.network.ActiveContract
import com.example.quickdraw.network.ActiveContractResponse
import com.example.quickdraw.network.AvailableContract
import com.example.quickdraw.network.AvailableContractResponse
import com.example.quickdraw.network.CONTRACTS_ACTIVE_ENDPOINT
import com.example.quickdraw.network.CONTRACTS_AVAILABLE_ENDPOINT
import com.example.quickdraw.network.CONTRACTS_REDEEM
import com.example.quickdraw.network.CONTRACTS_START
import com.example.quickdraw.network.ContractRedeemRequest
import com.example.quickdraw.network.ContractRedeemResponse
import com.example.quickdraw.network.ContractStartRequest
import com.example.quickdraw.network.INVENTORY_ENDPOINT
import com.example.quickdraw.network.InventoryBullet
import com.example.quickdraw.network.InventoryMedikit
import com.example.quickdraw.network.InventoryResponse
import com.example.quickdraw.network.InventoryUpgrade
import com.example.quickdraw.network.InventoryWeapon
import com.example.quickdraw.network.LEVELS_ENDPOINT
import com.example.quickdraw.network.PlayerStatus
import com.example.quickdraw.network.PrefKeys
import com.example.quickdraw.network.STATUS_ENDPOINT
import com.example.quickdraw.network.TAG
import com.example.quickdraw.network.TokenRequest
import com.example.quickdraw.network.toRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

class GameRepository(
    private val dataStore: DataStore<Preferences>
) {
    var player: PlayerStatus? = null
        private set
    var levels: List<Int>? = null
        private set
    var playerLevel: MutableStateFlow<Int> = MutableStateFlow(-3)
        private set
    var bullets: List<InventoryBullet>? = null
        private set
    var weapons: List<InventoryWeapon>? = null
        private set
    var medikits: List<InventoryMedikit>? = null
        private set
    var upgrades: List<InventoryUpgrade>? = null
        private set
    var activeContracts: List<ActiveContract>? = null
        private set
    var availableContracts: List<AvailableContract>? = null
        private set

    suspend fun getStatus() = withContext(Dispatchers.IO){
        val authToken = dataStore.data.map { pref -> pref[PrefKeys.authToken] }.firstOrNull()
        if(authToken == null){
            //Somehow go back to login screen? instead of failing silently
            Log.i(TAG, "Game Repository failed to get inventory")
            return@withContext
        }
        Log.i(TAG, "Repository auth token: $authToken")

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(STATUS_ENDPOINT)
            .post(TokenRequest(authToken).toRequestBody())
            .build()

        val response = client.newCall(request).execute()
        Log.i(TAG, response.code.toString())
        if(response.code == 200){
            //it should always be 200, otherwise there is a problem with the auth token
            val result = response.body!!.string()
            Log.i(TAG, result)
            val value = Json.decodeFromString<PlayerStatus>(result)
            player = value
        }
    }

    suspend fun getLevels() = withContext(Dispatchers.IO) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(LEVELS_ENDPOINT)
            .build()

        val response = client.newCall(request).execute()
        Log.i(TAG, response.code.toString())
        if(response.code == 200){
            //it should always be 200, otherwise there is a problem with the auth token
            val result = response.body!!.string()
            Log.i(TAG, result)
            val value = Json.decodeFromString<List<Int>>(result)
            levels = value
            playerLevel.value = getPlayerLevel()
        }
    }

    private fun getPlayerLevel(): Int {
        if(player == null || levels == null) {
            return -2;
        }
        var level = -1
        for(i in 0..<levels!!.size){
            if(player!!.exp >= levels!![i]){
                level = i + 1
            }
        }
        return level
    }

    suspend fun getInventory() = withContext(Dispatchers.IO) {
        val authToken = dataStore.data.map { pref -> pref[PrefKeys.authToken] }.firstOrNull()
        if(authToken == null){
            //Somehow go back to login screen? instead of failing silently
            Log.i(TAG, "Game Repository failed to get inventory")
            return@withContext
        }
        Log.i(TAG, "Repository auth token: $authToken")

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(INVENTORY_ENDPOINT)
            .post(TokenRequest(authToken).toRequestBody())
            .build()

        val response = client.newCall(request).execute()
        Log.i(TAG, response.code.toString())
        if(response.code == 200){
            //it should always be 200, otherwise there is a problem with the auth token
            val result = response.body!!.string()
            Log.i(TAG, result)
            val value = Json.decodeFromString<InventoryResponse>(result)
            //Separate values
            bullets = value.bullets
            weapons = value.weapons
            medikits = value.medikits
            upgrades = value.upgrades
        }
    }

    suspend fun getContracts() = withContext(Dispatchers.IO){
        getActiveContracts()
        getAvailableContracts()
    }

    private suspend fun getActiveContracts(){
        val authToken = dataStore.data.map { pref -> pref[PrefKeys.authToken] }.firstOrNull()
        if(authToken == null){
            //Somehow go back to login screen? instead of failing silently
            Log.i(TAG, "Game Repository failed to get active contracts")
            return
        }
        Log.i(TAG, "Repository auth token: $authToken")

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(CONTRACTS_ACTIVE_ENDPOINT)
            .post(TokenRequest(authToken).toRequestBody())
            .build()

        val response = client.newCall(request).execute()
        Log.i(TAG, response.code.toString())
        if(response.code == 200){
            //it should always be 200, otherwise there is a problem with the auth token
            val result = response.body!!.string()
            Log.i(TAG, result)
            val value = Json.decodeFromString<ActiveContractResponse>(result)
            activeContracts = value.contracts
        }
    }

    private suspend fun getAvailableContracts(){
        val authToken = dataStore.data.map { pref -> pref[PrefKeys.authToken] }.firstOrNull()
        if(authToken == null){
            //Somehow go back to login screen? instead of failing silently
            Log.i(TAG, "Game Repository failed to get available contracts")
            return
        }
        Log.i(TAG, "Repository auth token: $authToken")

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(CONTRACTS_AVAILABLE_ENDPOINT)
            .post(TokenRequest(authToken).toRequestBody())
            .build()

        val response = client.newCall(request).execute()
        Log.i(TAG, response.code.toString())
        if(response.code == 200){
            //it should always be 200, otherwise there is a problem with the auth token
            val result = response.body!!.string()
            Log.i(TAG, result)
            val value = Json.decodeFromString<AvailableContractResponse>(result)
            availableContracts = value.contracts
        }
    }

    suspend fun startContract(contract: AvailableContract) = withContext(Dispatchers.IO) {
        val authToken = dataStore.data.map { pref -> pref[PrefKeys.authToken] }.firstOrNull()
        if(authToken == null){
            //Somehow go back to login screen? instead of failing silently
            Log.i(TAG, "Game Repository failed to get available contracts")
            return@withContext
        }
        Log.i(TAG, "Repository auth token: $authToken")

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(CONTRACTS_START)
            //TODO: add chosen mercenaries to request
            .post(ContractStartRequest(authToken, contract.id, listOf(1)).toRequestBody())
            .build()

        val response = client.newCall(request).execute()
        Log.i(TAG, response.code.toString())
        val body = response.body!!.string()
        Log.i(TAG, body)
        if(response.code == 200){
            //it should always be 200, otherwise there is a problem with the flow not being correct
            //Contract successfully started
            availableContracts?.filter { ac -> contract.id != ac.id }
        }
    }

    suspend fun redeemContract(contract: ActiveContract) = withContext(Dispatchers.IO){
        val authToken = dataStore.data.map { pref -> pref[PrefKeys.authToken] }.firstOrNull()
        if(authToken == null){
            //Somehow go back to login screen? instead of failing silently
            Log.i(TAG, "Game Repository failed to get available contracts")
            return@withContext
        }
        Log.i(TAG, "Repository auth token: $authToken")

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(CONTRACTS_REDEEM)
            .post(ContractRedeemRequest(authToken, contract.activeId).toRequestBody())
            .build()

        val response = client.newCall(request).execute()
        Log.i(TAG, response.code.toString())
        if(response.code == 200){
            //it should always be 200, otherwise there is a problem with the flow not being correct
            //Check if the contract was successful
            val body = response.body!!.string()
            Log.i(TAG, body)
            val obj = Json.decodeFromString<ContractRedeemResponse>(body)
            if(obj.success){
                activeContracts?.filter { c -> contract.activeId != c.activeId }
            }
        }
    }

}