package com.example.quickdraw.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.quickdraw.common.PrefKeys
import com.example.quickdraw.common.dataStore
import com.example.quickdraw.login.LoginActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch(Dispatchers.Main) {
            val storedId = this@MainActivity.dataStore.data.map { pref -> pref[PrefKeys.playerId] }.firstOrNull()
            val tokenId = this@MainActivity.dataStore.data.map { pref -> pref[PrefKeys.authToken] }.firstOrNull()
            Log.i("QUICKDRAW", storedId.toString())
            Log.i("QUICKDRAW", tokenId.toString())
            if(storedId == null || tokenId == null){
                //send request to tokenLogin

                Log.i("QUICKDRAW", "Sending from Main to Login Activity")
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }
        setContent {

        }
    }
}
