package com.example.quickdraw.main.components

import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.quickdraw.ui.theme.progressBarColors

@Composable
fun TopBar() {
    Surface(color = MaterialTheme.colorScheme.surfaceContainer){
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            userScrollEnabled = false
            ) {
            //TODO: get this value from the text size instead
            val rowHeight = 24.dp
            item ( span = { GridItemSpan(2) } ) {
                RowWithProgressBar(rowHeight, progressBarColors.health)
            }
            item { CenteredText("Health") }
            item ( span = { GridItemSpan(2) } ) {
                RowWithProgressBar(rowHeight, progressBarColors.experience)
            }
            item { CenteredText("Player Level") }
            item { TopBarRow(image = Icons.Default.Done, text = "##Money##") }
            item { TopBarRow(image = Icons.Default.Done, text = "##Bounty##") }
            item { TopBarRow(image = Icons.Default.Done, text = "##Bullets##") }
        }
    }
}

@Composable
fun TopBarRow(image: ImageVector, text: String) {
    Row {
        Image(image, "")
        CenteredText(text)
    }
}

@Composable
fun RowWithProgressBar(height: Dp, color: Color) {
    Row(
        modifier = Modifier.heightIn(height).fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LinearProgressIndicator(
            progress = {0.5f},
            color = color,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun CenteredText(text: String) {
    Text(
        text,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}