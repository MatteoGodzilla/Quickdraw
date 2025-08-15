package com.example.quickdraw

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.lifecycleScope
import com.example.quickdraw.network.data.LoginResponse
import com.example.quickdraw.network.api.TOKEN_LOGIN_ENDPOINT
import com.example.quickdraw.network.data.TokenRequest
import com.example.quickdraw.network.api.toRequestBody
import com.example.quickdraw.game.GameActivity
import com.example.quickdraw.game.components.ScreenLoader
import com.example.quickdraw.game.vm.LoadingScreenVM
import com.example.quickdraw.login.LoginActivity
import com.example.quickdraw.network.ConnectionManager
import com.example.quickdraw.network.NoConnectionActivity
import com.example.quickdraw.ui.theme.QuickdrawTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.IOException

//Main as in first activity that is booted
class MainActivity : ComponentActivity() {

    val loadingScreenVM = LoadingScreenVM()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent{
            QuickdrawTheme {
                ScreenLoader(loadingScreenVM)
            }
        }

        lifecycleScope.launch(Dispatchers.IO) {
            loadFavoriteServer(this@MainActivity.dataStore)
            val tokenId = this@MainActivity.dataStore.data.map { pref -> pref[PrefKeys.authToken] }
                .firstOrNull()
            Log.i(TAG, "Stored Token = $tokenId")
            val username = this@MainActivity.dataStore.data.map { pref -> pref[PrefKeys.username] }
                .firstOrNull()
            Log.i(TAG, "Stored Username = $username")
            val level = this@MainActivity.dataStore.data.map { pref -> pref[PrefKeys.level] }
                .firstOrNull()
            Log.i(TAG, "Stored Level = $level")
            loadingScreenVM.showLoading()
            if(tokenId != null){
                try {
                    val response = ConnectionManager.attemptPost(TokenRequest(tokenId).toRequestBody(),TOKEN_LOGIN_ENDPOINT)
                    if(response == null){
                        //no connection available,send to no connection activity
                        loadingScreenVM.hideLoading()
                        val intent = Intent(this@MainActivity, NoConnectionActivity::class.java)
                        startActivity(intent)
                        return@launch;
                    }
                    else{
                        Log.i(TAG, "Token login returned with code ${response.code}")
                        if(response.code == 200){
                            val responseVal = Json.decodeFromString<LoginResponse>(response.body!!.string())
                            Log.i(TAG, "New valid token: $responseVal")
                            dataStore.edit { preferences ->
                                preferences[PrefKeys.authToken] = responseVal.authToken
                            }
                            dataStore.edit { preferences ->
                                preferences[PrefKeys.server] = ConnectionManager.getMainIP()
                            }
                            response.close()
                            Log.i(TAG, "Sending from Main to Game Activity")
                            loadingScreenVM.hideLoading()
                            val intent = Intent(this@MainActivity, GameActivity::class.java)
                            startActivity(intent)
                            return@launch;
                        }
                    }

                } catch (e: IOException){
                    Log.e(TAG, "there was an exception getting the url")
                    Log.e(TAG, e.toString())
                }
            }
            loadingScreenVM.hideLoading()
            Log.i(TAG, "Sending from Main to Login Activity")
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
