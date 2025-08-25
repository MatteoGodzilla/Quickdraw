package com.example.quickdraw.duelBandit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.lifecycleScope
import com.example.quickdraw.PrefKeys
import com.example.quickdraw.TAG
import com.example.quickdraw.dataStore
import com.example.quickdraw.game.GameActivity
import com.example.quickdraw.game.components.ScreenLoader
import com.example.quickdraw.loadFavoriteServer
import com.example.quickdraw.login.LoginActivity
import com.example.quickdraw.network.ConnectionManager
import com.example.quickdraw.network.NoConnectionActivity
import com.example.quickdraw.network.api.TOKEN_LOGIN_ENDPOINT
import com.example.quickdraw.network.api.toRequestBody
import com.example.quickdraw.network.data.LoginResponse
import com.example.quickdraw.network.data.TokenRequest
import com.example.quickdraw.ui.theme.QuickdrawTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.IOException

class DuelBanditActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
    }
}