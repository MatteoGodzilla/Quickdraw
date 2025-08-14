package com.example.quickdraw.game.vm

import androidx.compose.runtime.mutableStateOf


object LoadingScreenViewManager {
    val isLoading = mutableStateOf(false)

    fun showLoading() {
        isLoading.value = true
    }

    fun hideLoading() {
        isLoading.value = false
    }
}