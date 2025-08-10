package com.example.quickdraw.game

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quickdraw.dataStore
import com.example.quickdraw.duel.DuelActivity
import com.example.quickdraw.game.components.BasicScreen
import com.example.quickdraw.game.components.ContentTab
import com.example.quickdraw.game.screen.ContractsCallbacks
import com.example.quickdraw.game.screen.ContractsScreen
import com.example.quickdraw.network.data.ActiveContract
import com.example.quickdraw.network.data.AvailableContract
import com.example.quickdraw.game.screen.MainScreen
import com.example.quickdraw.game.screen.ShopCallbacks
import com.example.quickdraw.game.screen.ShopScreen
import com.example.quickdraw.game.screen.YourPlaceScreen
import com.example.quickdraw.game.viewmodels.LoadingScreenViewManager
import com.example.quickdraw.network.api.buyBulletsAPI
import com.example.quickdraw.network.api.toRequestBody
import com.example.quickdraw.network.data.HireableMercenary
import com.example.quickdraw.network.data.MercenaryHireable
import com.example.quickdraw.network.data.ShopBullet
import com.example.quickdraw.network.data.ShopMedikit
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

        //TODO: run repository fetch when it changes screen, not just at start

        lifecycleScope.launch {
            repository.getStatus()
            repository.getLevels()
            repository.getInventory()
            repository.getContracts()
            repository.getShopWeapons()
            repository.getShopBullets()
            repository.getShopMedikits()
            repository.getShopUpgrades()
            repository.getFriendLeaderboard()
            repository.getGlobalLeaderboard()
            repository.getHireableMercenaries()
            repository.getNextToUnlockMercenaries()
            repository.getPlayerEmployedMercenaries()
            repository.getUnassignedMercenaries()
        }

        setContent {
            val controller = rememberNavController()
            val isLoading by LoadingScreenViewManager.isLoading

            NavHost(navController = controller, startDestination = GameNavigation.Map) {
                composable<GameNavigation.YourPlace>{ YourPlaceScreen(controller, repository) }
                composable<GameNavigation.Shop> {
                    ShopScreen(controller, repository,object : ShopCallbacks{
                        override fun onBuyBullet(toBuy: ShopBullet) {
                            lifecycleScope.launch {
                                LoadingScreenViewManager.showLoading()
                                repository.buyBullet(toBuy)
                                LoadingScreenViewManager.hideLoading()
                            }
                        }

                        override fun onBuyMedikit(toBuy: ShopMedikit) {
                            lifecycleScope.launch { repository.buyMedikit(toBuy) }
                        }
                    })
                }
                composable<GameNavigation.Map> {
                    MainScreen(controller, repository){
                        val intent = Intent(this@GameActivity, DuelActivity::class.java)
                        startActivity(intent)
                    }
                }
                composable<GameNavigation.BountyBoard> {
                    BasicScreen("Bounty Board", controller, listOf(
                        ContentTab("Friends"){
                            if(repository.friendLeaderboard.isNotEmpty()){
                                Column (modifier = Modifier.padding(it)){
                                    for(entry in repository.friendLeaderboard) {
                                        Text(entry.toString())
                                    }
                                }
                            }
                        },
                        ContentTab("Leaderboard"){
                            if(repository.globalLeaderboard.isNotEmpty()){
                                Column (modifier = Modifier.padding(it)){
                                    for(entry in repository.globalLeaderboard) {
                                        Text(entry.toString())
                                    }
                                }
                            }
                        }
                    ))
                }
                composable<GameNavigation.Contracts> {
                    ContractsScreen(controller, repository, object : ContractsCallbacks {
                        override fun onRedeemContract(activeContract: ActiveContract) {
                            lifecycleScope.launch { repository.redeemContract(activeContract) }
                        }
                        override fun onStartContract(availableContract: AvailableContract,mercenaries:List<Int>) {
                            lifecycleScope.launch { repository.startContract(availableContract,mercenaries) }
                        }

                        override fun onHireMercenary(hireable: HireableMercenary) {
                            lifecycleScope.launch {
                                LoadingScreenViewManager.showLoading()
                                repository.employMercenary(hireable)
                                LoadingScreenViewManager.hideLoading()
                            }
                        }
                    })
                }
            }
        }
    }
}