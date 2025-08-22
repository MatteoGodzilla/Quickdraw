package com.example.quickdraw.duel

import android.content.Context
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quickdraw.QuickdrawApplication
import com.example.quickdraw.TAG
import com.example.quickdraw.duel.VMs.WeaponSelectionViewModel
import com.example.quickdraw.duel.components.PlayScreen
import com.example.quickdraw.duel.components.PresentationScreen
import com.example.quickdraw.duel.components.WeaponSelectionScreen
import com.example.quickdraw.ui.theme.QuickdrawTheme
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable


//This is a testing activity, should never be shown to user
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
    //@Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*
        val groupOwner = intent.getBooleanExtra(Game2Duel.groupOwnerKey, false)
        //The type-safe version of this is only available for Tiramisu
        val groupOwnerAddress = intent.getSerializableExtra(Game2Duel.groupOwnerAddressKey) as InetAddress

        val duelServer = DuelServer()
        lifecycleScope.launch {
            if(groupOwner){
                duelServer.startAsServer()
            } else {
                duelServer.startAsClient(groupOwnerAddress)
            }
        }
        */
        val duelGameLogic = DuelGameLogic()
        val duelServer = DuelServer(duelGameLogic)
        val qdapp = application as QuickdrawApplication
        val repository = qdapp.repository
        val vm = WeaponSelectionViewModel(qdapp.repository.inventory.weapons)
        enableEdgeToEdge()
        setContent{
            QuickdrawTheme {

                val localAddress = remember { mutableStateOf("") }
                val serverAddress = remember { mutableStateOf("") }
                val scope = rememberCoroutineScope()

                //val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                //localAddress.value = connectivityManager.getLinkProperties(connectivityManager.activeNetwork)?.linkAddresses.toString()

                val selfState = duelGameLogic.selfState.collectAsState().value
                val peerState = duelGameLogic.peerState.collectAsState().value
                if(selfState == DuelState.STEADY && peerState == DuelState.STEADY) startPolling(duelGameLogic)
                val controller = rememberNavController()
                NavHost(navController = controller, startDestination = DuelNavigation.Presentation){
                    composable<DuelNavigation.Presentation>{
                        PresentationScreen(controller,duelGameLogic,repository)
                    }

                    composable<DuelNavigation.WeaponSelect>{
                        WeaponSelectionScreen(controller,duelGameLogic,repository,vm)
                    }

                    composable<DuelNavigation.Play>{
                        PlayScreen(controller,duelGameLogic,repository)
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
        val qdapp = application as QuickdrawApplication
        qdapp.peerFinderSingleton.disconnectFromPeer()
    }
}
/*                Scaffold { padding ->

                }
*                     Column (modifier = Modifier.padding(padding)){
                        Text(localAddress.value)
                        Button(onClick = { scope.launch { duelServer.startAsServer() }}){
                            Text("Start as server")
                        }
                        TextField(value = serverAddress.value, onValueChange = { s-> serverAddress.value = s})

                        Button(onClick = { scope.launch { duelServer.startAsClient(InetAddress.getByName(serverAddress.value)) }}){
                            Text("Start as client")
                        }
                        RowDivider()
                        Text("Self: $selfState, Peer: $peerState")
                        Button(onClick = { scope.launch { duelGameLogic.setReady(10) } }) {
                            Text("Ready")
                        }
                        RowDivider()
                        Button(onClick = { scope.launch { duelGameLogic.bang() } }) {
                            Text("Bang")
                        }
                        RowDivider()
                        Button(onClick = { scope.launch { duelGameLogic.nextRound() } }) {
                            Text("Next round")
                        }


* */