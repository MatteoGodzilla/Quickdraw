package com.example.quickdraw.duel

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.quickdraw.PrefKeys
import com.example.quickdraw.dataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.net.InetAddress

//Used as test
class ScanningActivity : ComponentActivity() {

    private lateinit var peerFinder: PeerFinder
    private lateinit var username: String
    private var level: Int = 0

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val scanningFlow = MutableStateFlow(false)
        val peersFlow = MutableStateFlow<List<Peer>>(listOf())

        peerFinder = PeerFinder(this@ScanningActivity)
        peerFinder.onConnection { groupOwner, groupOwnerAddress ->
            if(groupOwner){
                //start as server
            } else {
                //start as client
            }
        }

        lifecycleScope.launch {
            //TODO: randomize name
            username = this@ScanningActivity.dataStore.data.map { pref -> pref[PrefKeys.username] }.firstOrNull() ?: "Guest"
            level = this@ScanningActivity.dataStore.data.map { pref -> pref[PrefKeys.level]?.toInt() }.firstOrNull() ?: 0
        }

        setContent {
            Scaffold { padding ->
                Column(modifier = Modifier
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                ) {
                    Text("Scanning:${scanningFlow.collectAsState().value}")
                    Text("Peer to Peer Devices:")
                    for(peer in peersFlow.collectAsState().value){
                        Text("${peer.username} (Level: ${peer.level})")
                        Button(onClick = { peerFinder?.startMatchWithPeer(peer) }) {
                            Text("Start round")
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        peerFinder.startScanning(Peer(username, level), this@ScanningActivity)
    }

    override fun onStop() {
        super.onStop()
        //TODO: change this so that it is kept active even while the app is closed
        //TODO: figure out updates in background
        peerFinder?.stopScanning()
    }
}
