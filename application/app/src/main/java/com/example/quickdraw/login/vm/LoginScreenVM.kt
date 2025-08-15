package com.example.quickdraw.login.vm

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickdraw.PrefKeys
import com.example.quickdraw.network.api.LOGIN_ENDPOINT
import com.example.quickdraw.network.data.LoginRequest
import com.example.quickdraw.network.data.LoginResponse
import com.example.quickdraw.TAG
import com.example.quickdraw.game.vm.LoadingScreenVM
import com.example.quickdraw.network.ConnectionManager
import com.example.quickdraw.network.api.toRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.IOException

class LoginScreenVM(
    private val dataStore: DataStore<Preferences>,
    private val onFailedLogin:()->Unit,
    private val onSuccessfulLogin: () -> Unit
) : ViewModel() {
    val email = mutableStateOf("")
    val password = mutableStateOf("")
    val showPassword = mutableStateOf(false)
    private val showInvalidCombo = mutableStateOf(false)

    fun canSendLogin(): Boolean{
        return email.value != "" && password.value != ""
    }

    fun sendLogin() = viewModelScope.launch(Dispatchers.IO) {
        val requestBody = LoginRequest(email.value, password.value).toRequestBody()
        try {
            val response=ConnectionManager.attemptPost(requestBody,LOGIN_ENDPOINT)
            if(response!=null){
                if(response.code != 200){
                    showInvalidCombo.value = true;
                } else {
                    val responseVal = Json.decodeFromString<LoginResponse>(response.body.string())
                    Log.i(TAG, responseVal.toString())
                    dataStore.edit { preferences ->
                        preferences[PrefKeys.authToken] = responseVal.authToken
                    }
                    response.close()
                    onSuccessfulLogin()
                }
            }
            else{
                onFailedLogin()
            }
        } catch (e: IOException){
            Log.e("QUICKDRAW", "there was an exception with login")
            Log.e("QUICKDRAW", e.toString())
        }
    }

}