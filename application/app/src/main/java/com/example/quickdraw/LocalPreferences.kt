package com.example.quickdraw

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.quickdraw.network.ConnectionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

val Context.dataStore by preferencesDataStore("login")

const val TAG = "QUICKDRAW"

class PrefKeys{
    companion object{
        val authToken = stringPreferencesKey("token")
        val username = stringPreferencesKey("username")
        val level = stringPreferencesKey("level")
        val server = stringPreferencesKey("server")
    }
}

suspend fun runIfAuthenticated(dataStore: DataStore<Preferences>, block: (authToken: String) -> Unit) =
    withContext(Dispatchers.IO) {
        val authToken = dataStore.data.map { pref -> pref[PrefKeys.authToken] }.firstOrNull()
        if (authToken != null) {
            block(authToken)
        } else {
            Log.e(TAG, "There was a problem retrieving the authToken")
        }
    }

suspend fun loadFavoriteServer(dataStore: DataStore<Preferences>){
    withContext(Dispatchers.IO) {
        val favourite = dataStore.data.map { pref -> pref[PrefKeys.server] }.firstOrNull()
        if(favourite!=null){
            ConnectionManager.setFavourite(favourite)
        }
    }
}

suspend fun signOff(dataStore: DataStore<Preferences>){
    dataStore.edit { pref->pref.remove(PrefKeys.authToken)}
}

object Game2Duel{
    val groupOwnerKey = "groupOwner"
    val groupOwnerAddressKey = "groupOwnerAddress"
}