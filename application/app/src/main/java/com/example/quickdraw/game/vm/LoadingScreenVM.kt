package com.example.quickdraw.game.vm

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel


class LoadingScreenVM: ViewModel() {
    val isLoading = mutableStateOf(false)

    fun showLoading() {
        isLoading.value = true
    }

    fun hideLoading() {
        isLoading.value = false
    }
}