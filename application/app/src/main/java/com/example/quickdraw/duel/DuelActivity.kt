package com.example.quickdraw.duel

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quickdraw.Game2Duel
import com.example.quickdraw.QuickdrawApplication
import com.example.quickdraw.TAG
import com.example.quickdraw.dataStore
import com.example.quickdraw.duel.components.PlayScreen
import com.example.quickdraw.duel.components.PresentationScreen
import com.example.quickdraw.duel.components.ResultsScreen
import com.example.quickdraw.duel.components.WeaponSelectionScreen
import com.example.quickdraw.duel.vms.WeaponSelectionViewModel
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
        val duelGameLogic = DuelGameLogic(
            repository.getPlayerAsPeer(),
            repository,
            this,
        )
        val duelServer = DuelServer(duelGameLogic)

        val isServer = intent.getBooleanExtra(Game2Duel.IS_SERVER_KEY, false)
        val serverAddress = intent.getStringExtra(Game2Duel.SERVER_ADDRESS_KEY)
        usingWifiP2P = intent.getBooleanExtra(Game2Duel.USING_WIFI_P2P, false)

        Log.i(TAG, "Started duel activity $isServer $serverAddress")

        enableEdgeToEdge()
        setContent{
            //force portrait
            this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

            LaunchedEffect(true) {
                if(isServer){
                    duelServer.startAsServer()
                } else {
                    duelServer.startAsClient(InetAddress.getByName(serverAddress))
                }
            }
            val controller = rememberNavController()
            val selfState = duelGameLogic.selfState.collectAsState().value
            val peerState = duelGameLogic.otherState.collectAsState().value
            val selfAsPeer = duelGameLogic.selfPeer.collectAsState().value
            val otherAsPeer = duelGameLogic.otherPeer.collectAsState().value
            switchNavigation(selfState, peerState, controller)
            NavHost(navController = controller, startDestination = DuelNavigation.Presentation){
                composable<DuelNavigation.Presentation>{
                    PresentationScreen(controller,selfAsPeer, otherAsPeer)
                }
                composable<DuelNavigation.WeaponSelect>{
                    val vm = viewModel {
                        WeaponSelectionViewModel(qdapp.repository.inventory.weapons.value, qdapp.repository.inventory.bullets.value, qdapp.imageLoader)
                    }
                    LaunchedEffect(true) {
                        lifecycleScope.launch {
                            duelGameLogic.setFavourite(this@DuelActivity.dataStore,vm)
                        }
                    }
                    WeaponSelectionScreen(selfAsPeer, otherAsPeer, duelGameLogic, repository,vm)
                }
                composable<DuelNavigation.Play>{
                    PlayScreen(controller, duelGameLogic)
                }
                composable<DuelNavigation.Results>{
                    ResultsScreen(controller, selfAsPeer, otherAsPeer, duelGameLogic, repository)
                }
            }
        }
    }

    fun switchNavigation(selfState: PeerState, peerState: PeerState, controller: NavHostController) {
        if(selfState == PeerState.CAN_PLAY && peerState == PeerState.CAN_PLAY){
            //initial
            controller.navigate(DuelNavigation.WeaponSelect)
        }
        if(selfState == PeerState.STEADY && peerState == PeerState.STEADY) {
            controller.navigate(DuelNavigation.Play)
        } else if (selfState == PeerState.DONE || peerState == PeerState.DONE){
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
