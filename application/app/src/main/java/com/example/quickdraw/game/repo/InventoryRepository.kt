package com.example.quickdraw.game.repo

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.quickdraw.TAG
import com.example.quickdraw.network.api.getInventoryAPI
import com.example.quickdraw.network.data.InventoryBullet
import com.example.quickdraw.network.data.InventoryMedikit
import com.example.quickdraw.network.data.InventoryUpgrade
import com.example.quickdraw.network.data.InventoryWeapon
import com.example.quickdraw.runIfAuthenticated
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class InventoryRepository (
    private val dataStore: DataStore<Preferences>
){
    var bullets: MutableStateFlow<List<InventoryBullet>> = MutableStateFlow(listOf())
        private set
    var weapons: MutableStateFlow<List<InventoryWeapon>> = MutableStateFlow(listOf())
        private set
    var medikits: MutableStateFlow<List<InventoryMedikit>> = MutableStateFlow(listOf())
        private set
    var upgrades: MutableStateFlow<List<InventoryUpgrade>> = MutableStateFlow(listOf())
        private set

    suspend fun getInventory() = runIfAuthenticated(dataStore) { auth ->
        val response = getInventoryAPI(auth)
        Log.i(TAG,response.toString())
        if (response != null) {
            //Separate values
            bullets.update { response.bullets }
            weapons.update { response.weapons }
            medikits.update { response.medikits }
            upgrades.update { response.upgrades }
        }
    }
}