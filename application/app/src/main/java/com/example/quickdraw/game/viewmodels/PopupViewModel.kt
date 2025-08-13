package com.example.quickdraw.game.viewmodels

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update


object PopupViewModel {
    val message: MutableStateFlow<String> = MutableStateFlow<String>("test")
    val isShowing:MutableStateFlow<Boolean> = MutableStateFlow<Boolean>(true)

    fun showLoading(msg:String) {
        message.update { x->msg }
        isShowing.update { x->true }
    }

    fun hide() {
        isShowing.update { x->false }
    }
}