package com.example.quickdraw.duel

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pManager.ActionListener
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.example.quickdraw.ui.theme.Typography
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.BufferedWriter
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException

class DuelActivity : ComponentActivity() {

    private lateinit var peerFinder: PeerFinder

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val scanningFlow = MutableStateFlow(false)
        val peersFlow = MutableStateFlow<List<Peer>>(listOf())

        peerFinder = PeerFinder(this, object : PeerFinderCallbacks{
            override fun onScanningChange(scanning: Boolean) {
                scanningFlow.value = scanning
            }
            override fun onPeerChange(newPeersList: List<Peer>) {
                peersFlow.value = newPeersList
            }
        })

        setContent {
            Scaffold { padding ->
                Column(modifier = Modifier
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                ) {
                    Text("Scanning:${scanningFlow.asStateFlow().collectAsState().value}")
                    Text("Peer to Peer Devices:")
                    for(peer in peersFlow.asStateFlow().collectAsState().value){
                        Text(peer.srcDevice.toString(), fontSize = Typography.titleLarge.fontSize)
                        Text("Instance name: ${peer.instanceName}")
                        Text("Registration type: ${peer.registrationType}")
                        Text("Full domain name: ${peer.fullDomainName}")
                        Text("Map: ${peer.txtRecordMap}")
                        Button(onClick = { peerFinder.startMatchWithPeer(peer) }) {
                            Text("Start round")
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        peerFinder.startScanning()
    }

    override fun onStop() {
        super.onStop()
        //TODO: change this so that it is kept active even while the app is closed
        //TODO: figure out updates in background
        peerFinder.stopScanning()
    }
}
