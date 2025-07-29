package com.example.quickdraw.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quickdraw.common.dataStore
import com.example.quickdraw.game.components.BasicScreen
import com.example.quickdraw.game.components.ContentTab
import com.example.quickdraw.game.contracts.ContractsScreen
import com.example.quickdraw.game.repo.GameRepository
import com.example.quickdraw.game.yourplace.YourPlaceScreen
import kotlinx.coroutines.launch
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
        val repository = GameRepository(dataStore)

        lifecycleScope.launch {
            repository.getInventory()
            repository.getContracts()
        }
        setContent {
            val controller = rememberNavController()

            NavHost(navController = controller, startDestination = Navigation.Map) {
                composable<Navigation.YourPlace>{ YourPlaceScreen(controller, repository) }
                composable<Navigation.Shop> {
                    BasicScreen("Shop", controller, listOf(
                        ContentTab("Weapons"){},
                        ContentTab("Bullets"){},
                        ContentTab("Medikits"){},
                        ContentTab("Upgrades"){}
                    ))
                }
                composable<Navigation.Map> { MainScreen(controller) }
                composable<Navigation.BountyBoard> {
                    BasicScreen("BountyBoard", controller, listOf(
                        ContentTab("Friends"){},
                        ContentTab("Leaderboard"){}
                    ))
                }
                composable<Navigation.Contracts> { ContractsScreen(controller, repository) }
            }
        }
    }
}