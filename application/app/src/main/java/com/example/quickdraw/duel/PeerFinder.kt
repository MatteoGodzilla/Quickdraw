package com.example.quickdraw.duel

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.WIFI_P2P_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.LocationManager
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.quickdraw.TAG
import kotlinx.coroutines.flow.MutableStateFlow
import java.net.InetAddress

class PeerFinder(
    private val context: Context
): WifiP2pManager.PeerListListener, WifiP2pManager.ConnectionInfoListener {

    var scanning: MutableStateFlow<Boolean> = MutableStateFlow(false)
        private set
    var rawDevices: MutableStateFlow<List<WifiP2pDevice>> = MutableStateFlow(listOf())
        private set
    private var onConnectionCallback: (Boolean, InetAddress) -> Unit = { b, address -> address}

    var p2pManager: WifiP2pManager = context.getSystemService(WIFI_P2P_SERVICE) as WifiP2pManager
    var channel = p2pManager.initialize(context, context.mainLooper, null)!!

    init {
        ContextCompat.registerReceiver(
            context,
            PeerFinderBroadcastReceiver(p2pManager, channel, this, this),
            PeerFinderBroadcastReceiver.getIntentFilter(),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        disconnectFromPeer()
    }

    @SuppressLint("MissingPermission")
    fun startScanning(activity: Activity) {
        Log.i(TAG, "[PeerFinder] Started Scanning")
        requestScanPermissions(activity)
        p2pManager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.i(TAG, "Success discovering peers")
            }
            override fun onFailure(p0: Int) {
                Log.i(TAG, "Failure discovering peers $p0")
            }
        })
    }

    fun stopScanning() {
        Log.i(TAG, "[PeerFinder] Stopped Scanning")
    }

    @SuppressLint("MissingPermission")
    fun startMatchWithPeer(peer: WifiP2pDevice) {
        Log.i(TAG, "[PeerFinder] Started match with peer")

        val config = WifiP2pConfig().apply {
            deviceAddress = peer.deviceAddress
            wps.setup = WpsInfo.PBC
        }

        p2pManager.connect(channel, config, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.i(TAG, "[PeerFinder] Successfully connected to device")
            }

            override fun onFailure(reason: Int) {
                Log.i(TAG, "[PeerFinder] Failed to connect to device $reason")
            }
        })
    }

    fun disconnectFromPeer() {
        Log.i(TAG, "[PeerFinder] Disconnecting from peer ")
        p2pManager.removeGroup(channel, object: WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.i(TAG, "[PeerFinder] Successfully removed group")
            }
            override fun onFailure(reason: Int) {
                Log.i(TAG, "[PeerFinder] Failed to remove group: $reason")
            }
        })
    }

    //INTERNAL
    private fun requestScanPermissions(helper: Activity){
        val permsToAsk = mutableListOf<String>()
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED){
            permsToAsk.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if(ContextCompat.checkSelfPermission(context, Manifest.permission.NEARBY_WIFI_DEVICES) != PERMISSION_GRANTED){
                permsToAsk.add(Manifest.permission.NEARBY_WIFI_DEVICES)
            }
        }

        if(permsToAsk.isNotEmpty()){
            ActivityCompat.requestPermissions(helper, permsToAsk.toTypedArray(), 0)
        }

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gpsActive = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if(!gpsActive){
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            context.startActivity(intent)
        }
    }

    override fun onPeersAvailable(newPeers: WifiP2pDeviceList?) {
        if(newPeers != null) {
            rawDevices.value = newPeers.deviceList.toList()
            Log.i(TAG, rawDevices.value.toString())
        }
    }

    override fun onConnectionInfoAvailable(info: WifiP2pInfo?) {
        Log.i(TAG, "[PeerFinder] Connection info")
        if(info != null && info.groupFormed && info.groupOwnerAddress != null){
            onConnectionCallback.invoke(info.isGroupOwner, info.groupOwnerAddress)
        }
    }

    //Add functions to callbacks
    fun onConnection(callback: (groupOwner: Boolean, groupOwnerAddress: InetAddress)->Unit){
        onConnectionCallback = callback
    }
}

class PeerFinderBroadcastReceiver(
    private val p2pManager: WifiP2pManager,
    private val channel: WifiP2pManager.Channel,
    private val peerReceiver: WifiP2pManager.PeerListListener,
    private val connectionListener: WifiP2pManager.ConnectionInfoListener
) : BroadcastReceiver() {
    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action){
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                p2pManager.requestPeers(channel, peerReceiver)
            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                p2pManager.requestConnectionInfo(channel, connectionListener)
            }
        }
    }


    companion object {
        fun getIntentFilter() : IntentFilter{
            return IntentFilter().apply {
                addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
                addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
            }
        }
    }
}