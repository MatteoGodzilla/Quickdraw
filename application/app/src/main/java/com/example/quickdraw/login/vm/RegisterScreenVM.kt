package com.example.quickdraw.login.vm

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickdraw.PrefKeys
import com.example.quickdraw.network.data.LoginResponse
import com.example.quickdraw.network.api.REGISTER_ENDPOINT
import com.example.quickdraw.network.data.RegisterRequest
import com.example.quickdraw.network.api.toRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class RegisterScreenVM(
    private val dataStore: DataStore<Preferences>,
    private val onRegisterSuccess: ()->Unit
) : ViewModel() {
    val username = mutableStateOf("")
    val email = mutableStateOf("")
    val password = mutableStateOf("")
    val showPassword = mutableStateOf(false)
    val passwordConfirm = mutableStateOf("")

    fun doPasswordsMatch(): Boolean {
         return password.value == passwordConfirm.value
    }

    fun canRegister(): Boolean{
        return username.value != "" && email.value != "" && password.value != "" && doPasswordsMatch()
    }

    fun register() = viewModelScope.launch(Dispatchers.IO){
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(REGISTER_ENDPOINT)
            .post(RegisterRequest(email.value, password.value, username.value).toRequestBody())
            .build()

        try {
            val response = client.newCall(request).execute()
            if(response.code != 200){
                Log.e("QUICKDRAW", response.code.toString())
            } else if(response.body != null){
                val responseVal = Json.decodeFromString<LoginResponse>(response.body!!.string())
                dataStore.edit { preferences ->
                    preferences[PrefKeys.authToken] = responseVal.authToken
                }
            }
            response.close()
            onRegisterSuccess()
        } catch (e: IOException){
            Log.e("QUICKDRAW", "there was an exception with registration")
            Log.e("QUICKDRAW", e.toString())
        }
    }

}