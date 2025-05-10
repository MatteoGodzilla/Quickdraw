package com.example.quickdraw.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.quickdraw.main.components.BottomNavBar
import com.example.quickdraw.ui.theme.QuickdrawTheme

@Preview
@Composable
fun MainScreen(){
    QuickdrawTheme {
        Scaffold(
            topBar = {
                Surface(color = MaterialTheme.colorScheme.surfaceContainer){
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        userScrollEnabled = false,

                    ) {
                        //TODO: get this value from the text size instead
                        val rowHeight = 24.dp
                        item (
                            span = { GridItemSpan(2) }
                        ) {
                            Row(
                                modifier = Modifier.heightIn(rowHeight).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                LinearProgressIndicator(
                                    progress = {0.5f},
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                        item {
                            Text(
                                "Health",
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        item (
                            span = { GridItemSpan(2) }
                        ) {
                            Row(
                                modifier = Modifier.heightIn(rowHeight).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                LinearProgressIndicator(
                                    progress = {0.5f},
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                        item {
                            Text(
                                "Player Level",
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        item{
                            Row {
                                Image(Icons.Default.Done, "")
                                Text(
                                    "##Money##",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                        item{
                            Row {
                                Image(Icons.Default.Done, "")
                                Text(
                                    "##Bounty##",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                        item{
                            Row {
                                Image(Icons.Default.Done, "")
                                Text(
                                    "##Bullets##",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

            },
            bottomBar = { BottomNavBar() }
        ) { padding ->
            Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End){
                IconButton(onClick = {}, modifier = Modifier.padding(padding)){
                    Icon(Icons.Default.Settings, "Settings")
                }
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(padding).fillMaxHeight(),
                verticalArrangement = Arrangement.Bottom
            ) {
                item {
                    Button(onClick = {}, colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        disabledContainerColor = MaterialTheme.colorScheme.secondary,
                        disabledContentColor = MaterialTheme.colorScheme.onSurface
                    )) {
                        Icon(Icons.Default.ShoppingCart,"Go to Jail")
                        Text("Go to Jail")
                    }
                }
                item {
                    Button(onClick = {}, colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        disabledContainerColor = MaterialTheme.colorScheme.primary,
                        disabledContentColor = MaterialTheme.colorScheme.onSurface
                    )) {
                        Icon(Icons.Default.MailOutline,"Duel")
                        Text("Duel")
                    }
                }
            }
        }
    }
}