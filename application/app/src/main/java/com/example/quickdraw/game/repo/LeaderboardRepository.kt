package com.example.quickdraw.game.repo

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.quickdraw.network.api.getFriendLeaderboardAPI
import com.example.quickdraw.network.api.getGlobalLeaderboardAPI
import com.example.quickdraw.network.data.LeaderboardEntry
import com.example.quickdraw.runIfAuthenticated
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LeaderboardRepository (
    private val dataStore: DataStore<Preferences>
){
    //Bounty board
    var friends: List<LeaderboardEntry> = listOf()
        private set
    var global: List<LeaderboardEntry> = listOf()
        private set

    suspend fun firstLoad(){
        getFriends()
        getGlobal()
    }

    suspend fun getFriends() = runIfAuthenticated(dataStore){ auth ->
        friends = getFriendLeaderboardAPI(auth)
    }

    suspend fun getGlobal() = withContext(Dispatchers.IO) {
        global = getGlobalLeaderboardAPI()
    }
}