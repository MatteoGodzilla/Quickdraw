package com.example.quickdraw.register

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickdraw.api.LoginResponse
import com.example.quickdraw.api.REGISTER_ENDPOINT
import com.example.quickdraw.api.RegisterRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class RegisterScreenVM : ViewModel() {
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
        //TODO: should refactor into a suspend method that returns Optional<RegisterResponse> so it is separated from gui
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