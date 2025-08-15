package com.example.quickdraw.game.vm

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class PopupVM: ViewModel() {
    val message: MutableStateFlow<String> = MutableStateFlow<String>("test")
    val isShowing: MutableStateFlow<Boolean> = MutableStateFlow<Boolean>(false)

    val isPositive: MutableStateFlow<Boolean> = MutableStateFlow<Boolean>(false)

    fun showLoading(msg:String,positive:Boolean=true) {
        message.update { msg }
        isShowing.update { true }
        isPositive.update { positive }
    }

    fun hide() {
        isShowing.update { false }
    }
}