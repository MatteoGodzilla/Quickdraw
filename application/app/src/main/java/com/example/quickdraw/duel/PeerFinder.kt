package com.example.quickdraw.duel

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pManager.ActionListener
import android.net.wifi.p2p.WifiP2pManager.Channel
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener
import android.net.wifi.p2p.WifiP2pManager.DnsSdServiceResponseListener
import android.net.wifi.p2p.WifiP2pManager.DnsSdTxtRecordListener
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest
import android.net.wifi.p2p.nsd.WifiP2pServiceInfo
import android.net.wifi.p2p.nsd.WifiP2pServiceRequest
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.quickdraw.TAG
import kotlinx.coroutines.flow.MutableStateFlow
import java.net.InetAddress

data class Peer(
    val username: String,
    val level: Int,
)

//Class responsible for finding other players nearby
@SuppressLint("MissingPermission")
class PeerFinder (
    private val context: Context,
) : DnsSdTxtRecordListener, DnsSdServiceResponseListener, ConnectionInfoListener {

    companion object {
        const val QUICKDRAW_SERVICE_TYPE = "_quickdraw_tcp"
        const val QUICKDRAW_INSTANCE_NAME = "Quickdraw"

        const val USERNAME_KEY = "username"
        const val LEVEL_KEY = "level"
    }

    var scanning: MutableStateFlow<Boolean> = MutableStateFlow(false)
        private set
    var peers: MutableStateFlow<List<Peer>> = MutableStateFlow(listOf(
        Peer("Testingl", 69)
    ))
        private set

    private var p2pManager: WifiP2pManager = context.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
    private var channel: Channel
    //This is awful, i know
    private var peerToDeviceAddress: MutableMap<Peer, String> = mutableMapOf()
    private var deviceAddressToTxt: MutableMap<String, Pair<String,Map<String, String>>> = mutableMapOf()
    private var deviceAddressToService: MutableMap<String, Pair<String,String>> = mutableMapOf()
    private var peerFinderBroadcastReceiver : PeerFinderBroadcastReceiver

    private var playerServiceInfo: WifiP2pServiceInfo? = null
    //listeners
    private var onConnectionListeners : MutableList< (groupOwner: Boolean, groupOwnerAddress: InetAddress)->Unit > = mutableListOf()

    init {
        Log.i(TAG, "manager: $p2pManager")
        channel = p2pManager.initialize(context, context.mainLooper, null)
        Log.i(TAG, "Channel: $channel")

        peerFinderBroadcastReceiver = PeerFinderBroadcastReceiver(p2pManager, channel, this)

        p2pManager.setDnsSdResponseListeners(channel, this, this)

        val serviceRequest: WifiP2pServiceRequest = WifiP2pDnsSdServiceRequest.newInstance()
        p2pManager.addServiceRequest(channel, serviceRequest, object: ActionListener {
            override fun onSuccess() {
                Log.i(TAG, "[PeerFinder] Successfully added Quickdraw request")
            }

            override fun onFailure(reason: Int) {
                Log.i(TAG, "[PeerFinder] there was a problem adding Quickdraw request: $reason")
            }
        })
    }

    fun startScanning(self: Peer, helper: Activity){
        if(scanning.value)
            return
        Log.i(TAG, "[PeerFinder] Started scanning")
        explainPermissionsToUser()
        requestScanPermissions(helper)

        ContextCompat.registerReceiver(
            context,
            peerFinderBroadcastReceiver,
            PeerFinderBroadcastReceiver.getIntentFilter(),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        playerServiceInfo = WifiP2pDnsSdServiceInfo.newInstance(
            QUICKDRAW_INSTANCE_NAME,
            QUICKDRAW_SERVICE_TYPE,
            mapOf(
                USERNAME_KEY to self.username,
                LEVEL_KEY to self.level.toString()
            )
        )
        p2pManager.addLocalService(channel, playerServiceInfo, object: ActionListener {
            override fun onSuccess() {
                Log.i(TAG, "[PeerFinder] Successfully added Quickdraw service")
            }
            override fun onFailure(reason: Int) {
                Log.i(TAG, "[PeerFinder] there was a problem adding Quickdraw service: $reason")
            }
        })

        p2pManager.discoverServices(channel, object : ActionListener {
            override fun onSuccess() {
                Log.i(TAG, "[PeerFinder] Successfully started discovering peers")
            }
            override fun onFailure(reason: Int) {
                Log.i(TAG, "[PeerFinder] There was an error discovering peers: $reason")
            }
        })
        scanning.value = true
    }

    fun stopScanning(){
        if(!scanning.value)
            return
        p2pManager.removeLocalService(channel, playerServiceInfo!!, object : ActionListener {
            override fun onSuccess() {
                Log.i(TAG, "[PeerFinder] Removed local service")
            }
            override fun onFailure(reason: Int) {
                Log.i(TAG, "[PeerFinder] There was an error removing local service: $reason")
            }
        })
        context.unregisterReceiver(peerFinderBroadcastReceiver)
        p2pManager.stopPeerDiscovery(channel, object: ActionListener {
            override fun onSuccess() {
                Log.i(TAG, "[PeerFinder] Stopped scanning")
            }
            override fun onFailure(reason: Int) {
                Log.i(TAG, "[PeerFinder] Failed to stop SCANNING: $reason")
            }
        })
        scanning.value = false
    }

    fun startMatchWithPeer(peer: Peer) {
        Log.i(TAG, "[PeerFinder] Started match with peer $peer")
        val config = WifiP2pConfig()
        config.deviceAddress = peerToDeviceAddress[peer]
        config.wps.setup = WpsInfo.PBC

        p2pManager.connect(channel, config, object: ActionListener {
            override fun onSuccess() {
                Log.i(TAG, "[PeerFinder] Successfully started match with peer $peer")
            }
            override fun onFailure(reason: Int) {
                Log.i(TAG, "[PeerFinder] There was a problem connecting with peer: $reason")
            }
        })
    }

    fun disconnectFromPeer(){
        Log.i(TAG, "[PeerFinder] Disconnecting from peer ")
        p2pManager.removeGroup(channel, object: ActionListener {
            override fun onSuccess() {
                Log.i(TAG, "[PeerFinder] Successfully removed group")
            }
            override fun onFailure(reason: Int) {
                Log.i(TAG, "[PeerFinder] Failed to remove group: $reason")
            }
        })
    }

    private fun explainPermissionsToUser(){
        //TODO
    }
    //Assume that this function is called when the user understands
    private fun requestScanPermissions(helper: Activity){
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(helper, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if(ContextCompat.checkSelfPermission(context, Manifest.permission.NEARBY_WIFI_DEVICES) != PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(helper, arrayOf(Manifest.permission.NEARBY_WIFI_DEVICES), 0)
            }
        }

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gpsActive = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if(!gpsActive){
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            context.startActivity(intent)
        }
    }

    private fun updatePeers() {
        val list: MutableList<Peer> = mutableListOf()
        peerToDeviceAddress.clear()
        for(dev in deviceAddressToService.keys) {
            if(deviceAddressToTxt.containsKey(dev)){
                val dictionary = deviceAddressToTxt[dev]?.second
                val username: String = dictionary?.get(USERNAME_KEY) ?: ""
                val level = dictionary?.get(LEVEL_KEY)?.toInt() ?: 0
                val peer = Peer(username, level)
                list.add(peer)
                peerToDeviceAddress[peer] = dev
            }
        }
        peers.value = list
        Log.i(TAG,"[PeerFinder] Updating Peers")
        for(p in peers.value){
            Log.i(TAG,p.toString())
        }
        Log.i(TAG,"-----------")
    }

    //Add functions to callbacks
    fun onConnection(callback: (groupOwner: Boolean, groupOwnerAddress: InetAddress)->Unit){
        onConnectionListeners.add(callback)
    }

    //Callbacks to receive data from android system
    //Called while discovering
    override fun onDnsSdTxtRecordAvailable(fullDomainName: String, txtRecordMap: MutableMap<String, String>, srcDevice: WifiP2pDevice) {
        deviceAddressToTxt[srcDevice.deviceAddress] = Pair(fullDomainName, txtRecordMap)
        Log.i(TAG, "[PeerServiceCallbacks] Txt: $fullDomainName $txtRecordMap $srcDevice")
        updatePeers()
    }

    override fun onDnsSdServiceAvailable(instanceName: String, registrationType: String, srcDevice: WifiP2pDevice) {
        deviceAddressToService[srcDevice.deviceAddress] = Pair(instanceName, registrationType)
        Log.i(TAG, "[PeerServiceCallbacks] Service: $instanceName $registrationType $srcDevice")
        updatePeers()
    }

    //Called while attempting to connect. Gets updated by the broadcast receiver
    override fun onConnectionInfoAvailable(info: WifiP2pInfo?) {
        Log.i(TAG, "[PeerServiceCallbacks] Connect info: $info")
        if(info == null || !info.groupFormed)
            return
        for(callback in onConnectionListeners){
            callback(info.isGroupOwner, info.groupOwnerAddress)
        }
    }


    //--- BROADCAST RECEIVER ---
    private class PeerFinderBroadcastReceiver(
        private val p2pManager: WifiP2pManager,
        private val channel: Channel,
        private val receiver: ConnectionInfoListener
    ) : BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent) {
            when(intent.action) {
                WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                    p2pManager.requestConnectionInfo(channel, receiver)
                }
            }
        }

        companion object {
            fun getIntentFilter(): IntentFilter {
                return IntentFilter().apply {
                    addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
                }
            }
        }
    }
}
