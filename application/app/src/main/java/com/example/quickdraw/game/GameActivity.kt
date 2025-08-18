package com.example.quickdraw.game

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings.ACTION_WIFI_SETTINGS
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.quickdraw.Game2Duel
import com.example.quickdraw.QuickdrawApplication
import com.example.quickdraw.TAG
import com.example.quickdraw.dataStore
import com.example.quickdraw.duel.DuelActivity
import com.example.quickdraw.duel.Peer
import com.example.quickdraw.game.components.Popup
import com.example.quickdraw.game.components.ScreenLoader
import com.example.quickdraw.game.repo.GameRepository
import com.example.quickdraw.game.screen.ContractsCallbacks
import com.example.quickdraw.game.screen.ContractsScreen
import com.example.quickdraw.game.screen.LeaderBoardScreen
import com.example.quickdraw.network.data.ActiveContract
import com.example.quickdraw.network.data.AvailableContract
import com.example.quickdraw.game.screen.MainScreen
import com.example.quickdraw.game.screen.ShopScreen
import com.example.quickdraw.game.screen.StartContractScreen
import com.example.quickdraw.game.screen.YourPlaceScreen
import com.example.quickdraw.game.vm.ContractStartVM
import com.example.quickdraw.game.vm.LoadingScreenVM
import com.example.quickdraw.game.vm.PopupVM
import com.example.quickdraw.game.vm.ShopScreenVM
import com.example.quickdraw.game.vm.YourPlaceVM
import com.example.quickdraw.login.LoginActivity
import com.example.quickdraw.network.data.HireableMercenary
import com.example.quickdraw.network.data.ShopBullet
import com.example.quickdraw.network.data.ShopMedikit
import com.example.quickdraw.network.data.ShopUpgrade
import com.example.quickdraw.network.data.ShopWeapon
import com.example.quickdraw.signOff
import com.example.quickdraw.ui.theme.QuickdrawTheme
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
    @Serializable
    data class StartContract(val idContract:Int)
}

class GameActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = GameRepository(dataStore)
        val popupVM = PopupVM()
        val loadScreenVM = LoadingScreenVM()

        //TODO: run repository fetch when it changes screen, not just at start
        lifecycleScope.launch {
            loadScreenVM.showLoading("Fetching resources...")
            repository.firstLoad()
            loadScreenVM.hideLoading()
        }

        val qdapp = application as QuickdrawApplication

        val onScoutingFun: ()->Unit ={
            if(qdapp.peerFinderSingleton.scanning.value){
                qdapp.peerFinderSingleton.stopScanning()
            } else {
                qdapp.peerFinderSingleton.startScanning(Peer(
                    repository.player.player.value.username ?: "ERROR",
                    repository.player.player.value.level
                ), this@GameActivity)
            }
        }

        qdapp.peerFinderSingleton.onConnection { groupOwner, groupOwnerAddress ->
            val intent = Intent(this, DuelActivity::class.java)
            intent.putExtra(Game2Duel.groupOwnerKey, groupOwner)
            intent.putExtra(Game2Duel.groupOwnerAddressKey, groupOwnerAddress)
            startActivity(intent)
        }

        setContent {
            val controller = rememberNavController()
            NavHost(navController = controller, startDestination = GameNavigation.Map) {

                composable<GameNavigation.YourPlace>{
                    val vm = viewModel { YourPlaceVM(repository, qdapp.imageLoader, this@GameActivity) }
                    YourPlaceScreen(vm, controller)
                }
                composable<GameNavigation.Shop> {
                    val vm = viewModel { ShopScreenVM(repository, qdapp.imageLoader) }
                    ShopScreen(vm, controller)
                }
                composable<GameNavigation.Map> {
                    MainScreen(controller, repository, qdapp.peerFinderSingleton,onScoutingFun){
                        try{
                            //not guaranteeded to exist
                            val i = Intent().apply {
                                component = ComponentName(
                                    "com.android.settings",
                                    "com.android.settings.wifi.p2p.WifiP2pSettings"
                                )
                            }
                            startActivity(i)
                        }
                        catch(e: ActivityNotFoundException){
                            val i = Intent(ACTION_WIFI_SETTINGS)
                            startActivity(i)
                        }
                    }
                }
                composable<GameNavigation.BountyBoard> {
                    LeaderBoardScreen(controller,repository, qdapp.imageLoader)
                }
                composable<GameNavigation.Contracts> {
                    ContractsScreen(controller, repository, qdapp.imageLoader, object : ContractsCallbacks {
                        override fun onRedeemContract(activeContract: ActiveContract) {
                            lifecycleScope.launch {
                                repository.contracts.redeem(activeContract)
                                val redeemedCoins = repository.contracts.lastRedeemed.value
                                if(redeemedCoins>0) popupVM.showLoading("Contract completed! You gained $redeemedCoins coins")
                                else popupVM.showLoading("Yor mercenaries failed the contract :(",false)
                            }
                        }
                        override fun onStartContract(availableContract: AvailableContract,mercenaries:List<Int>) {
                            lifecycleScope.launch { repository.contracts.start(availableContract,mercenaries) }
                            popupVM.showLoading("Contract started",true)
                        }

                        override fun onHireMercenary(hireable: HireableMercenary) {
                            lifecycleScope.launch {
                                repository.mercenaries.employ(hireable)
                                popupVM.showLoading("Mercenary hired!",true)
                            }
                        }
                    })
                }
                composable<GameNavigation.StartContract>{ backstackEntry ->
                    val selected = backstackEntry.toRoute<GameNavigation.StartContract>()
                    val contractsVM = ContractStartVM()
                    contractsVM.selectContract(selected.idContract)
                    StartContractScreen(controller,repository,contractsVM,object : ContractsCallbacks {
                        override fun onRedeemContract(activeContract: ActiveContract) {
                            lifecycleScope.launch {
                                repository.contracts.redeem(activeContract)
                            }
                        }
                        override fun onStartContract(availableContract: AvailableContract,mercenaries:List<Int>) {
                            lifecycleScope.launch { repository.contracts.start(availableContract,mercenaries) }
                        }

                        override fun onHireMercenary(hireable: HireableMercenary) {
                            lifecycleScope.launch {
                                repository.mercenaries.employ(hireable)
                            }
                        }
                    })
                }
            }
            //popup for all pages
            QuickdrawTheme {
                ScreenLoader(loadScreenVM)
                Popup(3000,popupVM)
                { popupVM.hide() }
            }
        }
    }
}