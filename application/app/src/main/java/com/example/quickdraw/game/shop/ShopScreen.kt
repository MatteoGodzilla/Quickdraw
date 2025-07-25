package com.example.quickdraw.game.shop

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.quickdraw.game.components.BasicScreen
import com.example.quickdraw.ui.theme.QuickdrawTheme

@Preview
@Composable
fun ShopScreen() {
    QuickdrawTheme {
        val tabs = listOf("Weapons", "Bullets", "Medikits", "Upgrades")
        BasicScreen(name = "Shop")
    }
}