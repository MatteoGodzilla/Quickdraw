package com.example.quickdraw.game.vm

import androidx.lifecycle.ViewModel

class GlobalPartsVM: ViewModel() {
    val popup = PopupVM()
    val loadScreen = LoadingScreenVM()
}