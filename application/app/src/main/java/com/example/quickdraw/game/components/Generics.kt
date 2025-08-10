package com.example.quickdraw.game.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.quickdraw.game.viewmodels.LoadingScreenViewManager


@Composable
fun HorizontalSeparator(){
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth().padding(all=0.dp),
        thickness = 2.dp,
        color=Color.Black
    )
}

//Experiment
@Composable
fun WaitForActionToFinishLoader(
    bgColor: Color = Color(0x88000000)
) {
    val isLoading by LoadingScreenViewManager.isLoading
    if (isLoading) {
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
                decorFitsSystemWindows = false,
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    strokeWidth = 8.dp,
                    color = Color.Yellow
                )
            }
        }
    }
}

