package com.example.quickdraw.login

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class LoginScreenVM : ViewModel() {
    val email = mutableStateOf("")
    val password = mutableStateOf("")
    val showPassword = mutableStateOf(false)

    fun canSendLogin(): Boolean{
        return email.value != "" && password.value != ""
    }

    fun sendLogin() = viewModelScope.launch(Dispatchers.IO) {
        val client = OkHttpClient()
        val request = Request.Builder().url("https://api.restful-api.dev/objects").build()
        try {
            val response = client.newCall(request).execute()
            Log.i("QUICKDRAW", response.body?.string() ?: "")
            response.close()
        } catch (e: IOException){
            Log.e("QUICKDRAW", "there was an exception getting the url")
        }
    }


}