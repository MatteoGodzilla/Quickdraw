package com.example.quickdraw.network

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.lifecycleScope
import com.example.quickdraw.MainActivity
import com.example.quickdraw.PrefKeys
import com.example.quickdraw.TAG
import com.example.quickdraw.dataStore
import com.example.quickdraw.game.GameActivity
import com.example.quickdraw.login.LoginActivity
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
import okhttp3.Request
import java.io.IOException

class NoConnectionActivity: ComponentActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuickdrawTheme {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize().background(color= MaterialTheme.colorScheme.surfaceContainer)
                ){
                    Text("Could not connect to server")
                    Button(onClick = {
                        val intent = Intent(this@NoConnectionActivity, MainActivity::class.java)
                        startActivity(intent)
                        return@Button
                    }) {
                        Text("Try again")
                    }
                }
            }
        }
    }
}