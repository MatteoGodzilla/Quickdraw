package com.example.quickdraw.game.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun HorizontalSeparator(){
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth().padding(all=0.dp),
        thickness = 2.dp,
        color=Color.Black
    )
}