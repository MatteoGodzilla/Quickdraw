package com.example.quickdraw.common

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore by preferencesDataStore("login")

const val TAG = "QUICKDRAW"

class PrefKeys{
    companion object{
        val authToken = stringPreferencesKey("token")
    }
}