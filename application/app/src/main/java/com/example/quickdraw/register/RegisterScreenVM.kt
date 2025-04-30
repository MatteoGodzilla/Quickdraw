package com.example.quickdraw.register

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

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

    fun register(){
        //check that all parameters were given

    }

}