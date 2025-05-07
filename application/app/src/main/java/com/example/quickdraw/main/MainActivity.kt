package com.example.quickdraw.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.quickdraw.login.LoginActivity
import com.example.quickdraw.common.PrefKeys
import com.example.quickdraw.common.dataStore
import com.example.quickdraw.ui.theme.QuickdrawTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //check for stored login info
        lifecycleScope.launch(Dispatchers.Main) {
            val storedId = this@MainActivity.dataStore.data.map { pref -> pref[PrefKeys.playerId] }.firstOrNull()
            val tokenId = this@MainActivity.dataStore.data.map { pref -> pref[PrefKeys.authToken] }.firstOrNull()
            if(storedId == null || tokenId == null){
                //send request to tokenLogin
                //if incorrect, go to login screen
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        setContent {
            QuickdrawTheme {
                Scaffold { padding ->
                    Column(modifier = Modifier.padding(padding)) {
                        Text("Logged in!")
                    }
                }
            }
        }
    }
}