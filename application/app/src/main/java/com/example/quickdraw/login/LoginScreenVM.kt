package com.example.quickdraw.login

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickdraw.api.LOGIN_ENDPOINT
import com.example.quickdraw.api.LoginRequest
import com.example.quickdraw.api.LoginResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class LoginScreenVM : ViewModel() {
    val email = mutableStateOf("")
    val password = mutableStateOf("")
    val showPassword = mutableStateOf(false)
    val showInvalidCombo = mutableStateOf(false)

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
                Log.e("QUICKDRAW", response.code.toString())
            } else if(response.body != null){
                val responseVal = Json.decodeFromString<LoginResponse>(response.body!!.string())
                Log.i("QUICKDRAW", responseVal.idPlayer.toString())
                Log.i("QUICKDRAW", responseVal.authToken)
            }
            response.close()
        } catch (e: IOException){
            Log.e("QUICKDRAW", "there was an exception getting the url")
            Log.e("QUICKDRAW", e.toString())
        }
    }


}