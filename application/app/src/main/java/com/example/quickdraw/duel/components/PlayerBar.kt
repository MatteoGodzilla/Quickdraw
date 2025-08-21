package com.example.quickdraw.duel.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.quickdraw.ui.theme.Typography

@Composable
fun DuelTopBar(opponent:String) {
    val rowHeight = 96.dp
    Surface(color = MaterialTheme.colorScheme.surfaceContainer,
        modifier= Modifier.fillMaxWidth().height(rowHeight)
    ){
        Box(modifier=Modifier.fillMaxSize()){
            Text(
                opponent,
                modifier = Modifier.fillMaxWidth().align(alignment = Alignment.Center),
                textAlign = TextAlign.Center,
                fontSize = Typography.titleLarge.fontSize
            )
        }
    }
}