package com.example.quickdraw.game.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.quickdraw.game.Navigation
import com.example.quickdraw.ui.theme.bottomBarButtonColors

@Preview
@Composable
fun BottomNavBar(navigation: NavHostController? = null, height: Dp = 100.dp){
    BottomAppBar(
        modifier = Modifier.height(height),
        contentPadding = PaddingValues(0.dp)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            verticalArrangement = Arrangement.Center,
            userScrollEnabled = false
        ) {
            //TODO: change button color depending on navigation
            // -> bool active on bottomBarButtonColors
            item {
                BottomBarTextButton(
                    function = { navigation?.navigate(Navigation.YourPlace) },
                    height = height,
                    color = bottomBarButtonColors(false),
                    content = {
                        BarIcon(
                            icon=Icons.Default.CheckCircle,
                            description="Your Place"
                        )
                    }
                )
            }
            item {
                BottomBarTextButton(
                    function = {navigation?.navigate(Navigation.BountyBoard)},
                    height = height,
                    color = bottomBarButtonColors(false),
                    content = {
                        BarIcon(
                            icon=Icons.Default.CheckCircle,
                            description="Bounty Board"
                        )
                    }
                )
            }
            item {
                BottomBarTextButton(
                    function = {navigation?.navigate(Navigation.Map)},
                    height = height,
                    color = bottomBarButtonColors(false),
                    content = {
                        BarIcon(
                            icon=Icons.Default.CheckCircle,
                            description="Map"
                        )
                    }
                )
            }
            item {
                BottomBarTextButton(
                    function = {navigation?.navigate(Navigation.Shop)},
                    height = height,
                    color = bottomBarButtonColors(false),
                    content = {
                        BarIcon(
                            icon=Icons.Default.CheckCircle,
                            description="Shop"
                        )
                    }
                )
            }
            item {
                BottomBarTextButton(
                    function = {navigation?.navigate(Navigation.Contracts)},
                    height = height,
                    color = bottomBarButtonColors(false),
                    content = {
                        BarIcon(
                            icon=Icons.Default.CheckCircle,
                            description="Contracts"
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun BarIcon(icon: ImageVector, description: String) {
    Icon (
        imageVector = icon,
        contentDescription = description
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