package com.example.quickdraw

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.lifecycleScope
import com.example.quickdraw.network.LoginResponse
import com.example.quickdraw.network.PrefKeys
import com.example.quickdraw.network.TAG
import com.example.quickdraw.network.TOKEN_LOGIN_ENDPOINT
import com.example.quickdraw.network.TokenRequest
import com.example.quickdraw.network.dataStore
import com.example.quickdraw.network.toRequestBody
import com.example.quickdraw.game.GameActivity
import com.example.quickdraw.login.LoginActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

//Main as in first activity that is booted
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch(Dispatchers.IO) {
            val tokenId = this@MainActivity.dataStore.data.map { pref -> pref[PrefKeys.authToken] }
                .firstOrNull()
            Log.i(TAG, "Stored Token = $tokenId")

            if(tokenId != null){
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url(TOKEN_LOGIN_ENDPOINT)
                    .post(TokenRequest(tokenId).toRequestBody())
                    .build()

                try {
                    val response = client.newCall(request).execute()
                    Log.i(TAG, "Token login returned with code ${response.code}")
                    if(response.code == 200){
                        val responseVal = Json.decodeFromString<LoginResponse>(response.body!!.string())
                        Log.i(TAG, "New valid token: $responseVal")
                        dataStore.edit { preferences ->
                            preferences[PrefKeys.authToken] = responseVal.authToken
                        }
                        response.close()
                        Log.i(TAG, "Sending from Main to Game Activity")
                        val intent = Intent(this@MainActivity, GameActivity::class.java)
                        startActivity(intent)
                        return@launch;
                    }
                } catch (e: IOException){
                    Log.e(TAG, "there was an exception getting the url")
                    Log.e(TAG, e.toString())
                }
            }
            Log.i(TAG, "Sending from Main to Login Activity")
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
