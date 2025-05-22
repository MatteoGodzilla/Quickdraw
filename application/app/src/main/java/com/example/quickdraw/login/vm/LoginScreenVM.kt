package com.example.quickdraw.login.vm

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickdraw.common.PrefKeys
import com.example.quickdraw.common.LOGIN_ENDPOINT
import com.example.quickdraw.common.LoginRequest
import com.example.quickdraw.common.LoginResponse
import com.example.quickdraw.common.toRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class LoginScreenVM(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    val email = mutableStateOf("")
    val password = mutableStateOf("")
    val showPassword = mutableStateOf(false)
    private val showInvalidCombo = mutableStateOf(false)

    fun canSendLogin(): Boolean{
        return email.value != "" && password.value != ""
    }

    fun sendLogin() = viewModelScope.launch(Dispatchers.IO) {
        //TODO: should refactor into a suspend method that returns Optional<LoginResponse> so it is separated from gui
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(LOGIN_ENDPOINT)
            .post(LoginRequest(email.value, password.value).toRequestBody())
            .build()

        try {
            val response = client.newCall(request).execute()
            if(response.code != 200){
                showInvalidCombo.value = true;
            } else if(response.body != null){
                val responseVal = Json.decodeFromString<LoginResponse>(response.body!!.string())
                dataStore.edit { preferences ->
                    preferences[PrefKeys.playerId] = responseVal.idPlayer
                    preferences[PrefKeys.authToken] = responseVal.authToken
                }
            }
            response.close()
            Log.i("QUICKDRAW", "STORED LOGIN INFO");
        } catch (e: IOException){
            Log.e("QUICKDRAW", "there was an exception getting the url")
            Log.e("QUICKDRAW", e.toString())
        }
    }

    //TODO: move this into a repository
    companion object{

    }

}