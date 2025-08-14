package com.example.quickdraw.game.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicTabLayout(
    selectedIndex: Int,
    tabs : List<String>,
    callback: (Int) -> Unit
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val minTabWidth = screenWidth / tabs.size
    ScrollableTabRow (
        selectedTabIndex = selectedIndex,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        edgePadding = 4.dp,
    ) {
        for(i in 0 ..< tabs.count()) {
            Tab(selected = false,modifier = Modifier.widthIn(min=minTabWidth), onClick = { callback(i) }, text = { Text(text = tabs[i],color= Color.Black, maxLines = 1) })
        }
    }
}