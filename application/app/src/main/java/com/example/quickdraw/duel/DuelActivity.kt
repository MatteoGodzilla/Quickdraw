package com.example.quickdraw.duel

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.quickdraw.Game2Duel
import com.example.quickdraw.QuickdrawApplication
import com.example.quickdraw.TAG
import kotlinx.coroutines.launch
import java.net.InetAddress

class DuelActivity : ComponentActivity() {
    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        enableEdgeToEdge()
        setContent{
            Scaffold { padding ->
                Column (modifier = Modifier.padding(padding)){
                    Button(onClick = { lifecycleScope.launch { duelServer.ready() } }) {
                        Text("Ready")
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        val qdapp = application as QuickdrawApplication
        qdapp.peerFinderSingleton.disconnectFromPeer()
    }
}