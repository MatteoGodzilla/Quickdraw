package com.example.quickdraw.common

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore by preferencesDataStore("login")

val TAG = "QUICKDRAW"

class PrefKeys{
    companion object{
        val playerId = intPreferencesKey("id")
        val authToken = stringPreferencesKey("token")
    }
}