package com.example.quickdraw.game

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quickdraw.Game2Duel
import com.example.quickdraw.QuickdrawApplication
import com.example.quickdraw.dataStore
import com.example.quickdraw.duel.DuelActivity
import com.example.quickdraw.duel.Peer
import com.example.quickdraw.game.components.BasicScreen
import com.example.quickdraw.game.components.ContentTab
import com.example.quickdraw.game.repo.GameRepository
import com.example.quickdraw.game.screen.ContractsCallbacks
import com.example.quickdraw.game.screen.ContractsScreen
import com.example.quickdraw.game.screen.LeaderBoardScreen
import com.example.quickdraw.network.data.ActiveContract
import com.example.quickdraw.network.data.AvailableContract
import com.example.quickdraw.game.screen.MainScreen
import com.example.quickdraw.game.screen.ShopCallbacks
import com.example.quickdraw.game.screen.ShopScreen
import com.example.quickdraw.game.screen.YourPlaceScreen
import com.example.quickdraw.game.viewmodels.LoadingScreenViewManager
import com.example.quickdraw.network.data.HireableMercenary
import com.example.quickdraw.network.data.ShopBullet
import com.example.quickdraw.network.data.ShopMedikit
import com.example.quickdraw.network.data.ShopUpgrade
import com.example.quickdraw.network.data.ShopWeapon
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

class GameActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = GameRepository(dataStore)

        //TODO: run repository fetch when it changes screen, not just at start
        lifecycleScope.launch {
            repository.firstLoad()
        }

        val qdapp = application as QuickdrawApplication
        qdapp.peerFinderSingleton.onConnection { groupOwner, groupOwnerAddress ->
            val intent = Intent(this, DuelActivity::class.java)
            intent.putExtra(Game2Duel.groupOwnerKey, groupOwner)
            intent.putExtra(Game2Duel.groupOwnerAddressKey, groupOwnerAddress)
            startActivity(intent)
        }

        setContent {
            val controller = rememberNavController()
            val isLoading by LoadingScreenViewManager.isLoading

            NavHost(navController = controller, startDestination = GameNavigation.Map) {
                composable<GameNavigation.YourPlace>{ YourPlaceScreen(controller, repository) }
                composable<GameNavigation.Shop> {
                    ShopScreen(controller, repository, object : ShopCallbacks{
                        override fun onBuyBullet(toBuy: ShopBullet) {
                            lifecycleScope.launch {
                                repository.shop.buyBullet(toBuy)
                            }
                        }

                        override fun onBuyMedikit(toBuy: ShopMedikit) {
                            lifecycleScope.launch { repository.shop.buyMedikit(toBuy) }
                        }

                        override fun onBuyWeapon(toBuy: ShopWeapon) {
                            lifecycleScope.launch { repository.shop.buyWeapon(toBuy) }
                        }

                        override fun onBuyUpgrade(toBuy: ShopUpgrade) {
                            lifecycleScope.launch { repository.shop.buyUpgrade(toBuy) }
                        }
                    })
                }
                composable<GameNavigation.Map> {
                    //TODO: pretty sure this will need to be kept alive between activities using Application
                    MainScreen(controller, repository, qdapp.peerFinderSingleton){
                        if(qdapp.peerFinderSingleton.scanning.value){
                            qdapp.peerFinderSingleton.stopScanning()
                        } else {
                            qdapp.peerFinderSingleton.startScanning(Peer(
                                repository.player.status.value?.username ?: "ERROR",
                                repository.player.level.value
                            ), this@GameActivity)
                        }
                    }
                }
                composable<GameNavigation.BountyBoard> {
                    LeaderBoardScreen(controller,repository)
                }
                composable<GameNavigation.Contracts> {
                    ContractsScreen(controller, repository, object : ContractsCallbacks {
                        override fun onRedeemContract(activeContract: ActiveContract) {
                            lifecycleScope.launch { repository.contracts.redeem(activeContract) }
                        }
                        override fun onStartContract(availableContract: AvailableContract,mercenaries:List<Int>) {
                            lifecycleScope.launch { repository.contracts.start(availableContract,mercenaries) }
                        }

                        override fun onHireMercenary(hireable: HireableMercenary) {
                            lifecycleScope.launch {
                                LoadingScreenViewManager.showLoading()
                                repository.mercenaries.employ(hireable)
                                LoadingScreenViewManager.hideLoading()
                            }
                        }
                    })
                }
            }
        }
    }
}