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
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.quickdraw.TAG
import com.example.quickdraw.game.repo.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.net.InetAddress


//Class responsible for finding other players nearby
@SuppressLint("MissingPermission")
class ServiceFinder (
    private val context: Context,
    private val channel:Channel,
    private val manager:WifiP2pManager,
    private val playerInfo: Peer
) : DnsSdTxtRecordListener, DnsSdServiceResponseListener, ConnectionInfoListener {

    companion object {
        const val QUICKDRAW_SERVICE_TYPE = "_quickdraw_tcp"
        const val QUICKDRAW_INSTANCE_NAME = "Quickdraw"

        const val ID_KEY = "id"
        const val USERNAME_KEY = "username"
        const val LEVEL_KEY = "level"
        const val HEALTH_KEY = "health"
        const val MAX_HEALTH_KEY = "maxHealth"
        const val BOUNTY_KEY = "bounty"
    }

    var scanning: MutableStateFlow<Boolean> = MutableStateFlow(false)
        private set
    var discoveredServices: MutableStateFlow<List<Peer>> = MutableStateFlow(listOf())
        private set

    //This is awful, i know
    private var deviceAddressToTxt: MutableMap<String, Pair<String,Map<String, String>>> = mutableMapOf()
    private var deviceAddressToService: MutableMap<String, Pair<String,String>> = mutableMapOf()

    var peers:MutableMap<String,Peer>  = mutableMapOf()
    private var peerFinderBroadcastReceiver : ServiceReceiver

    private var playerServiceInfo: WifiP2pServiceInfo? = null
    //listeners
    private var onConnectionListeners : MutableList< (groupOwner: Boolean, groupOwnerAddress: InetAddress)->Unit > = mutableListOf()
    private var APP_NAME = "quickdraw"
    init {
        val serviceInfo = WifiP2pDnsSdServiceInfo.newInstance(
            APP_NAME,  // Instance name
            "_quickDraw._tcp", // Service type (custom, can be "_myapp._tcp")
            playerInfo.getValuesAsMap()
        )

        manager.addLocalService(channel, serviceInfo, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.i(TAG, "[PeerFinder] Service registered:$APP_NAME")
            }
            override fun onFailure(reason: Int) {
                Log.e(TAG, "[PeerFinder] Failed to register service: $reason")
            }
        })

        peerFinderBroadcastReceiver = ServiceReceiver(manager, channel, this)
        manager.setDnsSdResponseListeners(channel, this, this)
    }

    fun discover(){
        val serviceRequest = WifiP2pDnsSdServiceRequest.newInstance()
        manager.clearServiceRequests(channel, null)
        manager.addServiceRequest(channel, serviceRequest, object : ActionListener {
            override fun onSuccess() {
                Log.i(TAG, "[PeerFinder] Added service request")
                // Start discovery
                manager.discoverServices(channel, object : ActionListener {
                    override fun onSuccess() {
                        Log.i(TAG, "[PeerFinder] Discovering services...")
                    }
                    override fun onFailure(reason: Int) {
                        Log.e(TAG, "[PeerFinder] Failed to discover services: $reason")
                    }
                })
            }
            override fun onFailure(reason: Int) {
                Log.e(TAG, "[PeerFinder] Failed to add service request: $reason")
            }
        })

        // Service discovery callbacks
        manager.setDnsSdResponseListeners(channel,
            { instanceName, registrationType, srcDevice ->
                Log.i(TAG, "[PeerFinder] Service found: $instanceName from ${srcDevice.deviceName}")
            },
            { fullDomain, txtRecordMap, srcDevice ->
                Log.i(TAG, "[PeerFinder] TXT Record: $txtRecordMap from ${srcDevice.deviceName}")
            }
        )

        manager.discoverServices(channel, object : ActionListener {
            override fun onSuccess() {
                Log.i(TAG, "[PeerFinder] Discovering services...")
            }
            override fun onFailure(reason: Int) {
                Log.e(TAG, "[PeerFinder] Failed to discover services: $reason")
            }
        })
    }

    fun startScanning(self: Peer, helper: Activity){
        /**
        if(scanning.value)
        return
        Log.i(TAG, "[PeerFinder] Started scanning")
        requestScanPermissions(helper)
        scanning.value = true
        /*
        localScope.launch {
        while(scanning.value) {
        startScanningLoop(self)
        delay(8000)
        stopScanningLoop()
        delay(100)
        }
        }
        */
        startScanningLoop(self)
         */
        discover()
    }

    fun stopDiscover(){

    }

    /**
     *     private fun startScanningLoop(self: Peer){
     *         Log.i(TAG, "[PeerFinder] Start Scanning Loop")
     *         if(!isRegistered.value){
     *             ContextCompat.registerReceiver(
     *                 context,
     *                 peerFinderBroadcastReceiver,
     *                 PeerFinderBroadcastReceiver.getIntentFilter(),
     *                 ContextCompat.RECEIVER_NOT_EXPORTED
     *             )
     *             isRegistered.update { true }
     *         }
     *
     *         playerServiceInfo = WifiP2pDnsSdServiceInfo.newInstance(
     *             QUICKDRAW_INSTANCE_NAME,
     *             QUICKDRAW_SERVICE_TYPE,
     *             self.getValuesAsMap()
     *         )
     *         manager.addLocalService(channel, playerServiceInfo, object: ActionListener {
     *             override fun onSuccess() {
     *                 Log.i(TAG, "[PeerFinder] Successfully added Quickdraw service")
     *             }
     *             override fun onFailure(reason: Int) {
     *                 Log.i(TAG, "[PeerFinder] there was a problem adding Quickdraw service: $reason")
     *             }
     *         })
     *
     *         manager.discoverServices(channel, object : ActionListener {
     *             override fun onSuccess() {
     *                 Log.i(TAG, "[PeerFinder] Successfully started discovering peers")
     *             }
     *             override fun onFailure(reason: Int) {
     *                 Log.i(TAG, "[PeerFinder] There was an error discovering peers: $reason")
     *             }
     *         })
     *     }
     */

    /**
     *     private fun stopScanningLoop(){
     *         manager.removeLocalService(channel, playerServiceInfo!!, object : ActionListener {
     *             override fun onSuccess() {
     *                 Log.i(TAG, "[PeerFinder] Removed local service")
     *             }
     *             override fun onFailure(reason: Int) {
     *                 Log.i(TAG, "[PeerFinder] There was an error removing local service: $reason")
     *             }
     *         })
     *
     *         if(isRegistered.value){
     *             isRegistered.update { false }
     *             context.unregisterReceiver(peerFinderBroadcastReceiver)
     *         }
     *
     *         manager.stopPeerDiscovery(channel, object: ActionListener {
     *             override fun onSuccess() {
     *                 Log.i(TAG, "[PeerFinder] Stopped scanning")
     *             }
     *             override fun onFailure(reason: Int) {
     *                 Log.i(TAG, "[PeerFinder] Failed to stop SCANNING: $reason")
     *             }
     *         })
     *     }
     */

    /**
     *     fun stopScanning(){
     *         if(!scanning.value)
     *             return
     *         stopDiscover()
     *         scanning.value = false
     *     }
     */

    /**
     *     fun startMatchWithPeer(peer: Peer) {
     *         Log.i(TAG, "[PeerFinder] Started match with peer $peer")
     *         val config = WifiP2pConfig()
     *         config.deviceAddress = peerToDeviceAddress[peer]
     *         config.wps.setup = WpsInfo.PBC
     *         manager.connect(channel, config, object: ActionListener {
     *             override fun onSuccess() {
     *                 Log.i(TAG, "[PeerFinder] Successfully started match with peer $peer")
     *             }
     *             override fun onFailure(reason: Int) {
     *                 Log.i(TAG, "[PeerFinder] There was a problem connecting with peer: $reason")
     *             }
     *         })
     *         stopScanning()
     *     }
     *
     *         private fun requestScanPermissions(helper: Activity){
     *         val permsToAsk = mutableListOf<String>()
     *         if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED){
     *             permsToAsk.add(Manifest.permission.ACCESS_FINE_LOCATION)
     *         }
     *
     *         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
     *             if(ContextCompat.checkSelfPermission(context, Manifest.permission.NEARBY_WIFI_DEVICES) != PERMISSION_GRANTED){
     *                 permsToAsk.add(Manifest.permission.NEARBY_WIFI_DEVICES)
     *             }
     *         }
     *
     *         if(permsToAsk.isNotEmpty()){
     *             ActivityCompat.requestPermissions(helper, permsToAsk.toTypedArray(), 0)
     *         }
     *
     *         val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
     *         val gpsActive = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
     *         if(!gpsActive){
     *             val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
     *             context.startActivity(intent)
     *         }
     *     }
     *
     *     fun disconnectFromPeer(){
     *         Log.i(TAG, "[PeerFinder] Disconnecting from peer ")
     *         manager.removeGroup(channel, object: ActionListener {
     *             override fun onSuccess() {
     *                 Log.i(TAG, "[PeerFinder] Successfully removed group")
     *             }
     *             override fun onFailure(reason: Int) {
     *                 Log.i(TAG, "[PeerFinder] Failed to remove group: $reason")
     *             }
     *         })
     *     }
     */





    private fun updatePeers() {
        peers.clear()
        for(dev in deviceAddressToService.keys) {
            if(deviceAddressToTxt.containsKey(dev)){
                val dictionary = deviceAddressToTxt[dev]?.second
                val id: Int = dictionary?.get(ID_KEY)?.toInt() ?: 0
                val username: String = dictionary?.get(USERNAME_KEY) ?: ""
                val level = dictionary?.get(LEVEL_KEY)?.toInt() ?: 0
                val health = dictionary?.get(HEALTH_KEY)?.toInt() ?: 100
                val maxHealth = dictionary?.get(MAX_HEALTH_KEY)?.toInt() ?: 100
                val bounty = dictionary?.get(BOUNTY_KEY)?.toInt() ?: 0
                val peer = Peer(id, username, level, health, maxHealth,bounty)
                peers[dev] = peer
            }
        }
        Log.i(TAG,"[PeerFinder] Updating Peers")
        for(p in discoveredServices.value){
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
        Log.i(TAG, "[PeerServiceCallbacks] Service: $instanceName $registrationType $srcDevice")
    }

    //Called while attempting to connect. Gets updated by the broadcast receiver
    override fun onConnectionInfoAvailable(info: WifiP2pInfo?) {
        Log.i(TAG, "[PeerServiceCallbacks] Connect info: $info")
        if(info == null || !info.groupFormed || info.groupOwnerAddress == null)
            return
        for(callback in onConnectionListeners){
            callback(info.isGroupOwner, info.groupOwnerAddress)
        }
    }

    //--- BROADCAST RECEIVER ---
    private class ServiceReceiver(
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
