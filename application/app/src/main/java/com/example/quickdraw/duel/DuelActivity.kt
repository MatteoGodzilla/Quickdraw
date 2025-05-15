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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.BufferedWriter
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException

class DuelActivity : ComponentActivity() {

    private lateinit var intentFilter: IntentFilter
    private lateinit var channel: WifiP2pManager.Channel
    private lateinit var p2pManager: WifiP2pManager
    private lateinit var myFooooo: Fooooo

    private var peers = mutableStateOf(WifiP2pDeviceList())
    private var groupFormed = false
    private var serverSocket : ServerSocket? = null

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.NEARBY_WIFI_DEVICES) != PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.NEARBY_WIFI_DEVICES), 0)
            }
        }

        intentFilter = IntentFilter()
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)

        p2pManager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        Log.i("QUICKDRAW", "manager: $p2pManager")
        channel = p2pManager.initialize(this, mainLooper, null)
        Log.i("QUICKDRAW", "Channel: $channel")
        startSearchingPeers()

        setContent {
            Scaffold { padding ->
                Column(modifier = Modifier
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                ) {
                    Text("Peer to Peer Devices:")
                    for (peer in peers.value.deviceList){
                        Text(peer.toString())
                        Button(onClick = {
                            val config = WifiP2pConfig().apply {
                                deviceAddress = peer.deviceAddress
                                wps.setup = WpsInfo.PBC
                            }
                            try{
                                p2pManager.connect(channel, config, object : ActionListener{
                                    override fun onSuccess() {
                                        Log.i("QUICKDRAW", "Connected to peer ${peer.deviceAddress}")
                                    }

                                    override fun onFailure(reason: Int) {
                                        Log.i("QUICKDRAW", "Could not connect to peer ${peer.deviceAddress}")
                                    }
                                })
                            } catch(_: Throwable){}
                        }) {
                            Text("Connect")
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        myFooooo = Fooooo(p2pManager, channel, object : FooCallbacks {
            @SuppressLint("MissingPermission")
            override fun onDiscoveryStatusChange(status: Int) {
                Log.i("QUICKDRAW", "Discovery Status: $status")
                if(status == WifiP2pManager.WIFI_P2P_DISCOVERY_STOPPED){
                    startSearchingPeers()
                }
            }

            override fun onChangePeers(peers: WifiP2pDeviceList) {
                this@DuelActivity.peers.value = peers
            }

            override fun onConnectionListener(connectionInfo: WifiP2pInfo?) {
                Log.i("QUICKDRAW", "$connectionInfo")
                groupFormed = connectionInfo?.groupFormed ?: false
                if(connectionInfo != null && connectionInfo.groupFormed){
                    if(connectionInfo.isGroupOwner && serverSocket == null){
                        lifecycleScope.launch (Dispatchers.IO){
                            //GAME SERVER
                            Log.i("QUICKDRAW", "Started Server")
                            serverSocket = ServerSocket()
                            serverSocket!!.reuseAddress = true
                            serverSocket!!.bind(InetSocketAddress(8888))
                            var client = serverSocket!!.accept()
                            while(client != null){
                                Log.i( "QUICKDRAW", "Accepted client: ${client.inetAddress}")
                                try {
                                    val clientIn = BufferedReader(client.getInputStream().reader())
                                    val clientOut = BufferedWriter(client.getOutputStream().writer())
                                    var m = clientIn.readLine()
                                    while(m != null){
                                        Log.i("QUICKDRAW", "<$m")
                                        clientOut.write(m)
                                        clientOut.flush()
                                        m = clientIn.readLine()
                                    }
                                    client.close()
                                    client = serverSocket!!.accept()
                                } catch(_: SocketException){
                                    break
                                }
                            }
                            Log.i("QUICKDRAW", "Server exited while loop")
                        }
                    } else if(connectionInfo.groupOwnerAddress != null){
                        lifecycleScope.launch (Dispatchers.IO) {
                            Log.i("QUICKDRAW", "Sending Echo ")
                            val socket = Socket(connectionInfo.groupOwnerAddress, 8888)
                            val socketOut = BufferedWriter(socket.getOutputStream().writer())
                            val socketIn = BufferedReader(socket.getInputStream().reader())
                            val message = "CIAO\n"
                            socketOut.write(message)
                            socketOut.flush()
                            Log.i("QUICKDRAW", ">${socketIn.readLine()}")
                            socket.close()
                        }
                    }
                } else {
                    Log.i("QUICKDRAW", "Group was not formed")
                    if(serverSocket != null){
                        serverSocket?.close()
                        serverSocket = null
                        Log.i( "QUICKDRAW", "Closing server because group was cancelled")
                    }
                    startSearchingPeers()
                }
            }
        })
        registerReceiver(myFooooo, intentFilter)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(myFooooo)
        p2pManager.removeGroup(channel, EmptyActionListener())
    }

    @SuppressLint("MissingPermission")
    fun startSearchingPeers(){
        if(!groupFormed){
            p2pManager.discoverPeers(channel, object : ActionListener {
                override fun onSuccess() {
                    Log.i("QUICKDRAW", "Successfully started discovering peers")
                }

                override fun onFailure(reason: Int) {
                    Log.i("QUICKDRAW", "There was an error discovering peers: $reason")
                }

            })
        }
    }
}

class EmptyActionListener : ActionListener{
    override fun onSuccess() {}
    override fun onFailure(reason: Int) {}
}
