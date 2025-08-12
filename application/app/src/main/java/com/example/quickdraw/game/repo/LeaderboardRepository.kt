package com.example.quickdraw.game.repo

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.quickdraw.network.api.getFriendLeaderboardAPI
import com.example.quickdraw.network.api.getGlobalLeaderboardAPI
import com.example.quickdraw.network.data.LeaderboardEntry
import com.example.quickdraw.runIfAuthenticated
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

class LeaderboardRepository (
    private val dataStore: DataStore<Preferences>
){
    //Bounty board
    var friends: MutableStateFlow<List<LeaderboardEntry>> = MutableStateFlow<List<LeaderboardEntry>>(listOf())
        private set
    var global: MutableStateFlow<List<LeaderboardEntry>> = MutableStateFlow<List<LeaderboardEntry>>(listOf())
        private set

    suspend fun firstLoad(){
        getFriends()
        getGlobal()
    }

    suspend fun getFriends() = runIfAuthenticated(dataStore){ auth ->
        friends.update{getFriendLeaderboardAPI(auth)}
    }

    suspend fun getGlobal() = withContext(Dispatchers.IO) {
        global.update{getGlobalLeaderboardAPI()}
    }
}