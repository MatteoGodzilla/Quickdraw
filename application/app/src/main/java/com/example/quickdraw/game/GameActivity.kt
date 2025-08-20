package com.example.quickdraw.game

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.quickdraw.Game2Duel
import com.example.quickdraw.QuickdrawApplication
import com.example.quickdraw.dataStore
import com.example.quickdraw.duel.DuelActivity
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
import com.example.quickdraw.game.vm.MainScreenVM
import com.example.quickdraw.game.vm.ShopScreenVM
import com.example.quickdraw.game.vm.YourPlaceVM
import com.example.quickdraw.music.AudioManager
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
    private lateinit var pbr : PermissionBroadcastReceiver
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = GameRepository(dataStore)
        val globalsVM = GlobalPartsVM()
        val qdapp = application as QuickdrawApplication

        //TODO: run repository fetch when it changes screen, not just at start
        lifecycleScope.launch {
            globalsVM.loadScreen.showLoading("Obtaining game data...")
            repository.firstLoad()
            globalsVM.loadScreen.hideLoading()
        }

        qdapp.peerFinderSingleton.onConnection { groupOwner, groupOwnerAddress ->
            val intent = Intent(this, DuelActivity::class.java)
            intent.putExtra(Game2Duel.groupOwnerKey, groupOwner)
            intent.putExtra(Game2Duel.groupOwnerAddressKey, groupOwnerAddress)
            startActivity(intent)
        }

        AudioManager.init(this,lifecycle)

        pbr = PermissionBroadcastReceiver(this)
        registerReceiver(pbr, PermissionBroadcastReceiver.getIntentFilter())

        setContent {
            //to place better
            val localAddress = remember { mutableStateOf("") }
            val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            localAddress.value = connectivityManager.getLinkProperties(connectivityManager.activeNetwork)?.linkAddresses!!.first { x->x.address is Inet4Address }.toString().split("/")[0]
            val musicContext = this
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
                    val vm = viewModel { MainScreenVM(repository, qdapp.peerFinderSingleton, this@GameActivity, pbr) }
                    MainScreen(vm, controller)
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
                //force portrait
                this@GameActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

                //global composables
                ScreenLoader(globalsVM.loadScreen)
                Popup(3000,globalsVM.popup)
                { }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(pbr)
    }
}
