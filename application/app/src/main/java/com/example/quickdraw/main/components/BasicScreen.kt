package com.example.quickdraw.main.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.quickdraw.ui.theme.QuickdrawTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicScreen(name: String = "Title", tabs: List<String>, selectedIndex:Int, callback: (Int) -> Unit){
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
            BasicTabLayout(paddingValues = padding, selectedIndex = selectedIndex, tabs = tabs, callback)
        }
    }
}