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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quickdraw.Game2Duel
import com.example.quickdraw.QuickdrawApplication
import com.example.quickdraw.TAG
import com.example.quickdraw.duel.VMs.WeaponSelectionViewModel
import com.example.quickdraw.duel.components.PlayScreen
import com.example.quickdraw.duel.components.PresentationScreen
import com.example.quickdraw.duel.components.WeaponSelectionScreen
import com.example.quickdraw.ui.theme.QuickdrawTheme
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.net.InetAddress

class DuelNavigation {
    @Serializable
    object Presentation

    @Serializable
    object DuelLobby

    @Serializable
    object WeaponSelect

    @Serializable
    object Play
}

class DuelActivity : ComponentActivity() {

    private var usingWifiP2P: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val qdapp = application as QuickdrawApplication
        val repository = qdapp.repository
        val player = repository.player.player.value
        val stats = repository.player.stats.value
        val selfAsPeer = Peer(player.username, player.level, player.health, stats.maxHealth)
        val duelGameLogic = DuelGameLogic(selfAsPeer)
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
                val selfState = duelGameLogic.selfState.collectAsState().value
                val peerState = duelGameLogic.peerState.collectAsState().value
                val otherAsPeer = duelGameLogic.otherPeer.collectAsState().value
                if(selfState == DuelState.STEADY && peerState == DuelState.STEADY) startPolling(duelGameLogic)

                val controller = rememberNavController()
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
                }
            }
        }
    }

    private fun startPolling(duelGameLogic: DuelGameLogic) = lifecycleScope.launch{
        while(duelGameLogic.selfState.value == DuelState.STEADY)  {
            val delta = System.currentTimeMillis() - duelGameLogic.referenceTimeMS - duelGameLogic.agreedBangDelay
            if(delta > 0){
                //start vibrating
                val vibrator = this@DuelActivity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
                Log.i(TAG, "Started vibrator")
                break;
            }
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