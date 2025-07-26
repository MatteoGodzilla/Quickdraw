package com.example.quickdraw.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quickdraw.game.components.BasicScreen
import kotlinx.serialization.Serializable

class Navigation {
    @Serializable
    object YourPlace
    @Serializable
    object BountyBoard
    @Serializable
    object Map
    @Serializable
    object Shop
    @Serializable
    object Contracts
}

class GameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Graph()
        }
    }
}

@Preview
@Composable
fun Graph(){
    val controller = rememberNavController()

    NavHost(navController = controller, startDestination = Navigation.Map) {
        composable<Navigation.YourPlace>{
            BasicScreen("Your Place", controller, listOf("Main", "Memories", "Inventory")){ index, padding ->
                when (index) {
                    0 -> {
                        Surface(
                            modifier = Modifier.fillMaxSize().padding(padding),
                            color = Color(255, 0, 0, 255)
                        ) {
                            Text("Main")
                        }
                    }
                    1 -> {
                        Text("Mamories", modifier = Modifier.padding(padding))
                    }
                    2 -> {
                        Text("STO CAZZO", modifier = Modifier.padding(padding))
                    }
                }
            }
        }
        composable<Navigation.Shop> {
            BasicScreen("Shop", controller, listOf("Weapons", "Bullets", "Medikits", "Upgrades"))
        }
        composable<Navigation.Map> {
            MainScreen(controller)
        }
        composable<Navigation.BountyBoard> {
            BasicScreen("BountyBoard", controller, listOf("Friends", "Leaderboard"))
        }
        composable<Navigation.Contracts> {
            BasicScreen("Contracts", controller, listOf("Active", "Available", "Mercenaries"))
        }
    }
}