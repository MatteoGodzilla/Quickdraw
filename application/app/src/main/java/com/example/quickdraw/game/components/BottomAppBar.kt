package com.example.quickdraw.game.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.quickdraw.R
import com.example.quickdraw.game.GameNavigation
import com.example.quickdraw.game.GameNavigation.*
import com.example.quickdraw.ui.theme.bottomBarButtonColors

@Composable
fun BottomNavBar(navigation: NavHostController, height: Dp = 96.dp){
    BottomAppBar(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .height(height)
    ) {
        val navBackStackEntry by navigation.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            verticalArrangement = Arrangement.Center,
            userScrollEnabled = false
        ) {
            item {
                BottomBarTextButton(
                    function = { navigation.navigate(YourPlace) },
                    height = height,
                    color = bottomBarButtonColors(currentDestination?.hasRoute<YourPlace>() == true),
                    content = {
                        BarIcon(
                            icon=R.drawable.home_24px,
                            description="Your Place"
                        )
                    }
                )
            }
            item {
                BottomBarTextButton(
                    function = {navigation.navigate(BountyBoard)},
                    height = height,
                    color = bottomBarButtonColors(currentDestination?.hasRoute<BountyBoard>() == true),
                    content = {
                        BarIcon(
                            icon=R.drawable.leaderboard_24px,
                            description="Bounty board"
                        )
                    }
                )
            }
            item {
                BottomBarTextButton(
                    function = {navigation.navigate(Map)},
                    height = height,
                    color = bottomBarButtonColors(currentDestination?.hasRoute<GameNavigation.Map>() == true),
                    content = {
                        BarIcon(
                            icon=R.drawable.baseline_map_24,
                            description="Map"
                        )
                    }
                )
            }
            item {
                BottomBarTextButton(
                    function = {navigation.navigate(Shop)},
                    height = height,
                    color = bottomBarButtonColors(currentDestination?.hasRoute<Shop>() == true),
                    content = {
                        BarIcon(
                            icon=R.drawable.shopping_bag_24px,
                            description="Shop"
                        )
                    }
                )
            }
            item {
                BottomBarTextButton(
                    function = {navigation.navigate(Contracts)},
                    height = height,
                    color = bottomBarButtonColors(currentDestination?.hasRoute<Contracts>() == true),
                    content = {
                        BarIcon(
                            icon=R.drawable.contract_24px,
                            description="Contracts"
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun BarIcon(@DrawableRes icon: Int, description: String) {
    Icon(
        imageVector = ImageVector.vectorResource(icon),
        description
    )
    Text(text = description, textAlign = TextAlign.Center)
}

@Composable
fun BottomBarTextButton(function: () -> Unit, height: Dp, color: ButtonColors, content: @Composable() (ColumnScope.() -> Unit)) {
    TextButton(onClick = function, contentPadding = PaddingValues(0.dp), colors = color) {
        Column(
            modifier = Modifier.heightIn(height),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            content = content
        )
    }
}