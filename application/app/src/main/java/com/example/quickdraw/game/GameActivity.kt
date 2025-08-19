package com.example.quickdraw.game

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings.ACTION_WIFI_SETTINGS
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.example.quickdraw.Game2Duel
import com.example.quickdraw.QuickdrawApplication
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
import com.example.quickdraw.game.screen.ManualConnectionScreen
import com.example.quickdraw.game.screen.ShopScreen
import com.example.quickdraw.game.screen.StartContractScreen
import com.example.quickdraw.game.screen.YourPlaceScreen
import com.example.quickdraw.game.vm.ContractStartVM
import com.example.quickdraw.game.vm.GlobalPartsVM
import com.example.quickdraw.game.vm.LeaderboardVM
import com.example.quickdraw.game.vm.LoadingScreenVM
import com.example.quickdraw.game.vm.PopupVM
import com.example.quickdraw.game.vm.ShopScreenVM
import com.example.quickdraw.game.vm.YourPlaceVM
import com.example.quickdraw.network.data.HireableMercenary
import com.example.quickdraw.ui.theme.QuickdrawTheme
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.net.Inet4Address

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
    @Serializable
    object ManualMatch
}

class GameActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = GameRepository(dataStore)
        val globalsVM = GlobalPartsVM()

        //TODO: run repository fetch when it changes screen, not just at start
        lifecycleScope.launch {
            globalsVM.loadScreen.showLoading("Fetching resources...")
            repository.firstLoad()
            globalsVM.loadScreen.hideLoading()
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

        val onSettingsFun: ()->Unit = {
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

        qdapp.peerFinderSingleton.onConnection { groupOwner, groupOwnerAddress ->
            val intent = Intent(this, DuelActivity::class.java)
            intent.putExtra(Game2Duel.groupOwnerKey, groupOwner)
            intent.putExtra(Game2Duel.groupOwnerAddressKey, groupOwnerAddress)
            startActivity(intent)
        }

        setContent {
            //to place better
            val localAddress = remember { mutableStateOf("") }
            val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            localAddress.value = connectivityManager.getLinkProperties(connectivityManager.activeNetwork)?.linkAddresses!!.first { x->x.address is Inet4Address }.toString().split("/")[0]

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
                    MainScreen(controller, repository, qdapp.peerFinderSingleton,onScoutingFun, onSettingsFun)
                }
                composable<GameNavigation.BountyBoard> {
                    val vm = viewModel { LeaderboardVM(repository, qdapp.imageLoader) }
                    LeaderBoardScreen(vm, controller)
                }
                composable<GameNavigation.Contracts> {
                    ContractsScreen(controller, repository, qdapp.imageLoader, object : ContractsCallbacks {
                        override fun onRedeemContract(activeContract: ActiveContract) {
                            lifecycleScope.launch {
                                repository.contracts.redeem(activeContract)
                                val redeemedCoins = repository.contracts.lastRedeemed.value
                                if(redeemedCoins>0) globalsVM.popup.showLoading("Contract completed! You gained $redeemedCoins coins")
                                else globalsVM.popup.showLoading("Yor mercenaries failed the contract :(",false)
                            }
                        }
                        override fun onStartContract(availableContract: AvailableContract,mercenaries:List<Int>) {
                            lifecycleScope.launch { repository.contracts.start(availableContract,mercenaries) }
                            globalsVM.popup.showLoading("Contract started",true)
                        }

                        override fun onHireMercenary(hireable: HireableMercenary) {
                            lifecycleScope.launch {
                                repository.mercenaries.employ(hireable)
                                globalsVM.popup.showLoading("Mercenary hired!",true)
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
                            globalsVM.loadScreen.showLoading("Starting...")
                            lifecycleScope.launch { repository.contracts.start(availableContract,mercenaries) }
                            globalsVM.loadScreen.hideLoading()
                        }

                        override fun onHireMercenary(hireable: HireableMercenary) {
                            lifecycleScope.launch {
                                repository.mercenaries.employ(hireable)
                            }
                        }
                    })
                }

                composable<GameNavigation.ManualMatch>{
                    ManualConnectionScreen(controller,repository,localAddress.value)
                }
            }
            //popup for all pages
            QuickdrawTheme {
                //force portait
                val context = LocalContext.current
                (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

                //global composables
                ScreenLoader(globalsVM.loadScreen)
                Popup(3000,globalsVM.popup)
                { }
            }
        }
    }
}