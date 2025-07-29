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
import kotlinx.coroutines.selects.select

data class ContentTab(val tabName: String, val content: @Composable (padding: PaddingValues)->Unit)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicScreen(
    name: String = "Title",
    controller: NavHostController,
    tabs: List<ContentTab>
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
            BasicTabLayout(paddingValues = padding, selectedIndex = selectedTab.intValue, tabs = tabs.map { it.tabName }){
                selectedTab.intValue = it
            }
            val newPadding = PaddingValues(
                top = padding.calculateTopPadding().plus(48.dp),
                start = padding.calculateStartPadding(LayoutDirection.Ltr),
                end = padding.calculateEndPadding(LayoutDirection.Ltr),
                bottom = 0.dp
            )

            tabs[selectedTab.intValue].content(newPadding)
        }
    }
}