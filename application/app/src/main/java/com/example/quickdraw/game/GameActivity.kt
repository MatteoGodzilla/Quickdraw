package com.example.quickdraw.game

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
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
import com.example.quickdraw.DEFAULT_VOLUME
import com.example.quickdraw.Game2Bandit
import com.example.quickdraw.Game2Duel
import com.example.quickdraw.PrefKeys
import com.example.quickdraw.QuickdrawApplication
import com.example.quickdraw.TAG
import com.example.quickdraw.dataStore
import com.example.quickdraw.duel.DuelActivity
import com.example.quickdraw.duel.duelBandit.DuelBanditActivity
import com.example.quickdraw.game.components.Popup
import com.example.quickdraw.game.components.ScreenLoader
import com.example.quickdraw.game.repo.GameRepository
import com.example.quickdraw.game.screen.ContractsScreen
import com.example.quickdraw.game.screen.DuelCallbacks
import com.example.quickdraw.game.screen.LeaderBoardScreen
import com.example.quickdraw.game.screen.MainScreen
import com.example.quickdraw.game.screen.ShopScreen
import com.example.quickdraw.game.screen.StartContractScreen
import com.example.quickdraw.game.screen.YourPlaceScreen
import com.example.quickdraw.game.vm.ContractStartVM
import com.example.quickdraw.game.vm.ContractsVM
import com.example.quickdraw.game.vm.GlobalPartsVM
import com.example.quickdraw.game.vm.LeaderboardVM
import com.example.quickdraw.game.vm.MainScreenVM
import com.example.quickdraw.game.vm.ShopScreenVM
import com.example.quickdraw.game.vm.YourPlaceVM
import com.example.quickdraw.music.AudioManager
import com.example.quickdraw.music.AudioManagerLifecycleObserver
import com.example.quickdraw.network.data.ActiveContract
import com.example.quickdraw.network.data.AvailableContract
import com.example.quickdraw.notifications.QDNotifManager
import com.example.quickdraw.ui.theme.QuickdrawTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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
    data class StartContract(val contractId: Int)
}

class GameActivity : ComponentActivity(){

    private var pbr : PermissionBroadcastReceiver? = null
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val globalsVM = GlobalPartsVM()
        val qdapp = application as QuickdrawApplication

        qdapp.repository = GameRepository(dataStore)
        val repository = qdapp.repository

        //TODO: run repository fetch when it changes screen, not just at start
        lifecycleScope.launch {
            globalsVM.loadScreen.showLoading("Obtaining game data...")
            repository.firstLoad()
            globalsVM.loadScreen.hideLoading()
        }

        QDNotifManager.init(this)

        runBlocking {
            val mute = dataStore.data.map { pref -> pref[PrefKeys.musicMute] }.first() ?: false
            val bgmVolume = dataStore.data.map { pref -> pref[PrefKeys.musicVolume] }.first() ?: DEFAULT_VOLUME
            val sfxVolume = dataStore.data.map { pref -> pref[PrefKeys.sfxVolume] }.first() ?: DEFAULT_VOLUME
            Log.i(TAG, "Initial volume: $bgmVolume $sfxVolume")
            //Creating the audio streams
            AudioManager.init(this@GameActivity, bgmVolume, sfxVolume)
            //Attaching the audio manager lifecycle to game activity lifecycle
            AudioManagerLifecycleObserver.init(this@GameActivity.lifecycle)
            if(!mute){
                //actually starting the audio
                AudioManagerLifecycleObserver.attach()
            }
        }

        //For when another peer is connecting through wifi-p2p
        qdapp.peerFinderSingleton.onConnection { groupOwner, groupOwnerAddress ->
            qdapp.peerFinderSingleton.stopScanning()
            goToDuel(groupOwner, groupOwnerAddress.hostAddress!!)
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
                    val vm = viewModel { MainScreenVM(repository, qdapp.peerFinderSingleton, this@GameActivity, pbr!!) }
                    MainScreen(vm, controller,object: DuelCallbacks{
                        override fun onScan() {
                            vm.onScan()
                        }

                        override fun onScanBandits() {
                            lifecycleScope.launch{
                                globalsVM.popup.showLoading("Locating bandits...",false)
                                repository.bandits.getBandits()
                                globalsVM.popup.showLoading("Found ${repository.bandits.bandits.value.size} bandits!",true)
                            }
                        }

                        override fun onDuel() {

                        }

                        override fun onDuelBandit(id:Int) {
                            goToBanditDuel(id)
                        }
                    })
                }
                composable<GameNavigation.BountyBoard> {
                    val vm = viewModel { LeaderboardVM(repository, qdapp.imageLoader) }
                    LeaderBoardScreen(vm, controller)
                }
                composable<GameNavigation.Contracts> {
                    val vm = viewModel { ContractsVM( repository, globalsVM, controller) }
                    ContractsScreen(vm, controller, repository, qdapp.imageLoader)
                }
                composable<GameNavigation.StartContract>{ backstackEntry ->
                    val selected = backstackEntry.toRoute<GameNavigation.StartContract>()
                    val vm = viewModel { ContractStartVM(selected.contractId, controller, globalsVM, repository, this@GameActivity) }
                    StartContractScreen(vm, controller)
                }
            }
            //popup for all pages
            QuickdrawTheme {
                //force portrait
                this@GameActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

                //global composables
                ScreenLoader(globalsVM.loadScreen)
                Popup(3000,globalsVM.popup){}
            }
        }
    }

    private fun goToDuel(isServer: Boolean, address: String){
        val intent = Intent(this, DuelActivity::class.java)
        intent.putExtra(Game2Duel.IS_SERVER_KEY, isServer)
        intent.putExtra(Game2Duel.SERVER_ADDRESS_KEY, address)
        intent.putExtra(Game2Duel.USING_WIFI_P2P, true)
        startActivity(intent)
        finish()
    }

    private fun goToBanditDuel(id:Int){
        val intent = Intent(this, DuelBanditActivity::class.java)
        intent.putExtra(Game2Bandit.BANDIT_ID, id)
        startActivity(intent)
    }

    override fun onResume(){
        super.onResume()

        pbr = PermissionBroadcastReceiver(this)
        registerReceiver(pbr, PermissionBroadcastReceiver.getIntentFilter())
    }

    override fun onPause() {
        super.onPause()
        if(pbr != null){
            unregisterReceiver(pbr)
            pbr = null
        }
    }
}
