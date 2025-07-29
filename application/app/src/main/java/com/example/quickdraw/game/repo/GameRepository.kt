package com.example.quickdraw.game.repo

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.quickdraw.common.BASE_URL
import com.example.quickdraw.common.PrefKeys
import com.example.quickdraw.common.TAG
import com.example.quickdraw.common.TokenRequest
import com.example.quickdraw.common.toRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

const val INVENTORY_ENDPOINT = "$BASE_URL/inventory"
const val CONTRACTS_ACTIVE_ENDPOINT = "$BASE_URL/contracts/active"
const val CONTRACTS_AVAILABLE_ENDPOINT = "$BASE_URL/contracts/available"

class GameRepository(
    private val dataStore: DataStore<Preferences>
) {
    var bullets: List<InventoryBullet>? = null
        private set
    var weapons: List<InventoryWeapon>? = null
        private set
    var medikits: List<InventoryMedikit>? = null
        private set
    var upgrades: List<InventoryUpgrade>? = null
        private set
    var activeContracts: List<Contract>? = null
        private set
    var availableContracts: List<Contract>? = null
        private set

    suspend fun getInventory() = withContext(Dispatchers.IO) {
        val authToken = dataStore.data.map { pref -> pref[PrefKeys.authToken] }.firstOrNull()
        if(authToken == null){
            //Somehow go back to login screen? instead of failing silently
            Log.i(TAG, "Game Repository failed to get inventory")
            return@withContext;
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
            return;
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
            val value = Json.decodeFromString<ContractResponse>(result)
            activeContracts = value.contracts
        }
    }

    private suspend fun getAvailableContracts(){
        val authToken = dataStore.data.map { pref -> pref[PrefKeys.authToken] }.firstOrNull()
        if(authToken == null){
            //Somehow go back to login screen? instead of failing silently
            Log.i(TAG, "Game Repository failed to get available contracts")
            return;
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
            val value = Json.decodeFromString<ContractResponse>(result)
            availableContracts = value.contracts
        }
    }

    private fun getAuthToken(){

    }
}