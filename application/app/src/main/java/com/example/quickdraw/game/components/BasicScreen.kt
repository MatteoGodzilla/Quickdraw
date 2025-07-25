package com.example.quickdraw.game.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.quickdraw.ui.theme.QuickdrawTheme

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicScreen(
    name: String = "Title",
    controller: NavHostController? = null,
    destinations: List<String> = listOf(),
    content: @Composable (Int, PaddingValues) -> Unit = { i, paddingValues -> }
){
    val selectedTab = remember { mutableIntStateOf(0) }
    QuickdrawTheme {
        Scaffold (
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(name) },

                )
            },
            bottomBar = { BottomNavBar(navigation = controller) },
        ) { padding ->
            BasicTabLayout(paddingValues = padding, selectedIndex = selectedTab.intValue, tabs = destinations){
                selectedTab.intValue = it
            }
            val newPadding = PaddingValues(
                top = padding.calculateTopPadding().plus(48.dp),
                start = padding.calculateStartPadding(LayoutDirection.Ltr),
                end = padding.calculateEndPadding(LayoutDirection.Ltr),
                bottom = padding.calculateBottomPadding()
            )

            content(selectedTab.intValue, newPadding)
        }
    }
}