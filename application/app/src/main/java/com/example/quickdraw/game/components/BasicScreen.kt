package com.example.quickdraw.game.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.quickdraw.ui.theme.QuickdrawTheme

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicScreen(name: String = "Title"){
    QuickdrawTheme {
        Scaffold (
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(name) },
                    navigationIcon = {
                        IconButton(onClick = {}) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    }
                )
            },
            bottomBar = { BottomNavBar() }
        ) { padding ->
            BasicTabLayout(paddingValues = padding, selectedIndex = 0, tabs = listOf()){}
        }
    }
}