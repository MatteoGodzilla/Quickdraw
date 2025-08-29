package com.example.quickdraw.game.vm

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class PopupVM: ViewModel() {
    val message = MutableStateFlow("test")
    val isShowing = MutableStateFlow(false)

    val isPositive = MutableStateFlow(false)

    fun showLoading(msg:String,positive:Boolean=true) {
        message.update { msg }
        isShowing.update { true }
        isPositive.update { positive }
    }

    fun hide() {
        isShowing.update { false }
    }
}