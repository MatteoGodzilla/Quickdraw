package com.example.quickdraw.duel

import android.content.Context
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quickdraw.Game2Duel
import com.example.quickdraw.QuickdrawApplication
import com.example.quickdraw.TAG
import com.example.quickdraw.duel.VMs.WeaponSelectionViewModel
import com.example.quickdraw.duel.components.PlayScreen
import com.example.quickdraw.duel.components.PresentationScreen
import com.example.quickdraw.duel.components.ResultsScreen
import com.example.quickdraw.duel.components.WeaponSelectionScreen
import com.example.quickdraw.ui.theme.QuickdrawTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.net.InetAddress

class DuelNavigation {
    @Serializable
    object Presentation

    @Serializable
    object WeaponSelect

    @Serializable
    object Play

    @Serializable
    object Results
}

class DuelActivity : ComponentActivity() {

    private var usingWifiP2P: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val qdapp = application as QuickdrawApplication
        val repository = qdapp.repository
        val player = repository.player.player.value
        val stats = repository.player.stats.value
        val duelGameLogic = DuelGameLogic(Peer(player.username, player.level, player.health, stats.maxHealth),
         3, this) //for now the rounds are fixed
        val duelServer = DuelServer(duelGameLogic)

        val isServer = intent.getBooleanExtra(Game2Duel.IS_SERVER_KEY, false)
        val serverAddress = intent.getStringExtra(Game2Duel.SERVER_ADDRESS_KEY)
        usingWifiP2P = intent.getBooleanExtra(Game2Duel.USING_WIFI_P2P, false)

        val vm = WeaponSelectionViewModel(qdapp.repository.inventory.weapons)
        enableEdgeToEdge()
        setContent{
            LaunchedEffect(true) {
                if(isServer){
                    duelServer.startAsServer()
                } else {
                    duelServer.startAsClient(InetAddress.getByName(serverAddress))
                }
            }
            QuickdrawTheme {
                val controller = rememberNavController()
                val selfState = duelGameLogic.selfState.collectAsState().value
                val peerState = duelGameLogic.peerState.collectAsState().value
                val selfAsPeer = duelGameLogic.selfPeer.collectAsState().value
                val otherAsPeer = duelGameLogic.otherPeer.collectAsState().value
                switchNavigation(selfState, peerState, controller)

                NavHost(navController = controller, startDestination = DuelNavigation.Presentation){
                    composable<DuelNavigation.Presentation>{
                        PresentationScreen(controller,selfAsPeer, otherAsPeer)
                    }
                    composable<DuelNavigation.WeaponSelect>{
                        WeaponSelectionScreen(controller,selfAsPeer, otherAsPeer, duelGameLogic, repository,vm)
                    }
                    composable<DuelNavigation.Play>{
                        PlayScreen(duelGameLogic)
                    }
                    composable<DuelNavigation.Results>{
                        ResultsScreen(controller, selfAsPeer, otherAsPeer, duelGameLogic)
                    }
                }
            }
        }
    }

    fun switchNavigation(selfState: DuelState, peerState: DuelState, controller: NavHostController) {
        if(selfState == DuelState.STEADY && peerState == DuelState.STEADY) {
            controller.navigate(DuelNavigation.Play)
        } else if (selfState == DuelState.BANG && peerState == DuelState.BANG){
            controller.navigate(DuelNavigation.Results)
        } else if (selfState == DuelState.DONE && peerState == DuelState.DONE){
            controller.navigate(DuelNavigation.Results)
        }
    }

    override fun onStop() {
        super.onStop()
        if(usingWifiP2P){
            //Destroy the connection
            val qdapp = application as QuickdrawApplication
            qdapp.peerFinderSingleton.disconnectFromPeer()
        }
    }
}