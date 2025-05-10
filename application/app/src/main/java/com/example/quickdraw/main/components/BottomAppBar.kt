package com.example.quickdraw.main.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.quickdraw.ui.theme.bottomBarButtonColors

@Preview
@Composable
fun BottomNavBar(height: Dp = 100.dp){
    BottomAppBar(
        modifier = Modifier.height(height),
        contentPadding = PaddingValues(0.dp),
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            verticalArrangement = Arrangement.Center,
            userScrollEnabled = false,

        ) {
            //TODO: change button color depending on navigation
            // -> bool active on bottomBarButtonColors
            item {
                TextButton(onClick = {}, contentPadding = PaddingValues(0.dp), colors = bottomBarButtonColors(false)) {
                    Column(
                        modifier = Modifier.heightIn(height),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Your Place",
                            modifier = Modifier.size(height/3,height/2)
                        )
                        Text("Your Place", textAlign = TextAlign.Center)
                    }
                }

            }
            item {
                TextButton(onClick = {}, contentPadding = PaddingValues(0.dp), colors = bottomBarButtonColors(false)) {
                    Column(
                        modifier = Modifier.heightIn(height),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Bounty Board",
                            modifier = Modifier.size(height/3,height/2)
                        )
                        Text("Bounty Board", textAlign = TextAlign.Center)
                    }
                }
            }
            item {
                TextButton(onClick = {}, contentPadding = PaddingValues(0.dp), colors = bottomBarButtonColors(true)) {
                    Column(
                        modifier = Modifier.heightIn(height),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Map",
                            modifier = Modifier.size(height/3,height/2)
                        )
                        Text("Map", textAlign = TextAlign.Center)
                    }
                }
            }
            item {
                TextButton(onClick = {}, contentPadding = PaddingValues(0.dp), colors = bottomBarButtonColors(false)){
                    Column(
                        modifier = Modifier.heightIn(height),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Shop",
                            modifier = Modifier.size(height/3,height/2)
                        )
                        Text("Shop", textAlign = TextAlign.Center)
                    }
                }
            }
            item {
                TextButton(onClick = {}, contentPadding = PaddingValues(0.dp), colors = bottomBarButtonColors(false)) {
                    Column(
                        modifier = Modifier.heightIn(height),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Contracts",
                            modifier = Modifier.size(height/3,height/2)
                        )
                        Text("Contracts", textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}