package com.example.quickdraw.duel

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.quickdraw.QuickdrawApplication
import com.example.quickdraw.game.components.RowDivider
import com.example.quickdraw.ui.theme.QuickdrawTheme
import kotlinx.coroutines.launch
import java.net.InetAddress


//This is a testing activity, should never be shown to user
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

        enableEdgeToEdge()
        setContent{
            QuickdrawTheme {
                Screen(duelServer, duelGameLogic, this)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        val qdapp = application as QuickdrawApplication
        qdapp.peerFinderSingleton.disconnectFromPeer()
    }
}

@Composable
fun Screen(duelServer: DuelServer, duelGameLogic: DuelGameLogic, context: Context) {
    val localAddress = remember { mutableStateOf("") }
    val serverAddress = remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    localAddress.value = connectivityManager.getLinkProperties(connectivityManager.activeNetwork)?.linkAddresses.toString()

    Scaffold { padding ->
        Column (modifier = Modifier.padding(padding)){
            Text(localAddress.value)
            Button(onClick = { scope.launch { duelServer.startAsServer() }}){
                Text("Start as server")
            }
            TextField(value = serverAddress.value, onValueChange = { s-> serverAddress.value = s})

            Button(onClick = { scope.launch { duelServer.startAsClient(InetAddress.getByName(serverAddress.value)) }}){
                Text("Start as client")
            }
            RowDivider()
            Button(onClick = { scope.launch { duelGameLogic.ready() } }) {
                Text("Ready")
            }
        }
    }
}