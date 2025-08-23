package com.example.quickdraw.game.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.quickdraw.R
import com.example.quickdraw.game.GameNavigation
import com.example.quickdraw.ui.theme.QuickdrawTheme
import kotlinx.coroutines.launch

data class ContentTab(val tabName: String, val content: @Composable ()->Unit)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicScreen(
    name: String = "Title",
    controller: NavHostController,
    tabs: List<ContentTab>,
    money:Int = 0,
    showMoney: Boolean = false
){
    val pagerState = rememberPagerState (initialPage = 0){ tabs.count() }
    val scrollScope = rememberCoroutineScope()

    QuickdrawTheme {
        Scaffold (
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(name) },
                    actions = { if(showMoney) MoneyDisplayer(money) },
                    modifier = Modifier.padding(0.dp),
                    navigationIcon = {
                        if(controller.currentDestination!!.hasRoute<GameNavigation.StartContract>()){
                            IconButton(onClick = { controller.navigate(GameNavigation.Contracts) }) {
                                Icon(Icons.AutoMirrored.Default.ArrowBack,"go back")
                            }
                        }
                    }
                )
            },
            bottomBar = { BottomNavBar(navigation = controller) },
        ) { padding ->
            Column(
                modifier = Modifier.padding(padding)
            ){
                BasicTabLayout( selectedIndex = pagerState.currentPage, tabs = tabs.map { it.tabName } ){
                    scrollScope.launch { pagerState.animateScrollToPage(it) }
                }
                HorizontalPager(
                    state = pagerState,
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    Column ( modifier = Modifier.verticalScroll(rememberScrollState()) ){
                        tabs[page].content()
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun Prev() {
    BasicScreen("Preview", rememberNavController(), listOf(
        ContentTab("Weapons"){
            Text("hi <3", modifier = Modifier.padding())
        },
        ContentTab("Testing"){
            Text("hi <3")
        },
    ))
}
