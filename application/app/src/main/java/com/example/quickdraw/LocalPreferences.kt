package com.example.quickdraw

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.quickdraw.network.ConnectionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

val Context.dataStore by preferencesDataStore("login")

const val TAG = "QUICKDRAW"
const val DEFAULT_VOLUME = 0.5f

class PrefKeys{
    companion object{
        val authToken = stringPreferencesKey("token")
        val username = stringPreferencesKey("username")
        val level = stringPreferencesKey("level")
        val server = stringPreferencesKey("server")
        val musicVolume = floatPreferencesKey("music")
        val sfxVolume = floatPreferencesKey("sfx")
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
    const val IS_SERVER_KEY = "groupOwner"
    const val SERVER_ADDRESS_KEY = "groupOwnerAddress"
    const val USING_WIFI_P2P = "wifi-direct?"
}

object Game2Bandit{
    const val BANDIT_ID = "bandit_id"
    const val BANDIT_HP = "bandit_hp"
    const val BANDIT_MIN_DAM= "bandit_min_dam"
    const val BANDIT_MAX_DAM = "bandit_max_dam"
    const val BANDIT_MIN_SPEED = "bandit_min_spd"
    const val BANDIT_MAX_SPEED = "bandit_max_spd"
    const val NAME = "name"
}