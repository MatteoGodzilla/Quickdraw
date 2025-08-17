package com.example.quickdraw.game.vm

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel


class LoadingScreenVM: ViewModel() {
    val isLoading = mutableStateOf(false)
    val message = mutableStateOf("")

    fun showLoading(msg:String="Connecting...") {
        isLoading.value = true
        message.value=msg
    }

    fun hideLoading() {
        isLoading.value = false
    }
}