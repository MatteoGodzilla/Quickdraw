package com.example.quickdraw.game

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quickdraw.network.dataStore
import com.example.quickdraw.duel.DuelActivity
import com.example.quickdraw.game.components.BasicScreen
import com.example.quickdraw.game.components.ContentTab
import com.example.quickdraw.game.screen.ContractsCallbacks
import com.example.quickdraw.game.screen.ContractsScreen
import com.example.quickdraw.network.ActiveContract
import com.example.quickdraw.network.AvailableContract
import com.example.quickdraw.game.screen.MainScreen
import com.example.quickdraw.game.screen.YourPlaceScreen
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

class GameNavigation {
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
            repository.getStatus()
            repository.getInventory()
            repository.getContracts()
        }
        setContent {
            val controller = rememberNavController()

            NavHost(navController = controller, startDestination = GameNavigation.Map) {
                composable<GameNavigation.YourPlace>{ YourPlaceScreen(controller, repository) }
                composable<GameNavigation.Shop> {
                    BasicScreen("Shop", controller, listOf(
                        ContentTab("Weapons"){},
                        ContentTab("Bullets"){},
                        ContentTab("Medikits"){},
                        ContentTab("Upgrades"){}
                    ))
                }
                composable<GameNavigation.Map> {
                    MainScreen(controller, repository){
                        val intent = Intent(this@GameActivity, DuelActivity::class.java)
                        startActivity(intent)
                    }
                }
                composable<GameNavigation.BountyBoard> {
                    BasicScreen("Bounty Board", controller, listOf(
                        ContentTab("Friends"){},
                        ContentTab("Leaderboard"){}
                    ))
                }
                composable<GameNavigation.Contracts> {
                    ContractsScreen(controller, repository, object : ContractsCallbacks {
                        override fun onRedeemContract(activeContract: ActiveContract) {
                            lifecycleScope.launch { repository.redeemContract(activeContract) }
                        }
                        override fun onStartContract(availableContract: AvailableContract) {
                            lifecycleScope.launch { repository.startContract(availableContract) }
                        }
                    })
                }
            }
        }
    }
}