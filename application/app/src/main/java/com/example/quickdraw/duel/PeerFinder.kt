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
import android.net.wifi.p2p.nsd.WifiP2pServiceRequest
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.quickdraw.network.TAG


data class Peer(
    val srcDevice: WifiP2pDevice,
    val instanceName: String,
    val registrationType: String,
    val fullDomainName: String,
    val txtRecordMap: Map<String, String>
)

interface PeerFinderCallbacks {
    fun onScanningChange(scanning: Boolean)
    fun onPeerChange(newPeersList: List<Peer>)
}

//Class responsible for finding other players nearby
@SuppressLint("MissingPermission")
class PeerFinder (
    private val activity: Activity,
    private val callbacks: PeerFinderCallbacks
) : DnsSdTxtRecordListener, DnsSdServiceResponseListener, WifiP2pManager.ConnectionInfoListener {

    private var peers: List<Peer> = listOf()
        set(value) { callbacks.onPeerChange(value) }
    private var scanning: Boolean = false
        set(value) { callbacks.onScanningChange(value) }

    private var p2pManager: WifiP2pManager = activity.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
    private var channel: Channel
    private var deviceToTxt: MutableMap<WifiP2pDevice, Pair<String,Map<String, String>>> = mutableMapOf()
    private var deviceToService: MutableMap<WifiP2pDevice, Pair<String,String>> = mutableMapOf()
    private var peerFinderBroadcastReceiver : PeerFinderBroadcastReceiver

    init {
        Log.i(TAG, "manager: $p2pManager")
        channel = p2pManager.initialize(activity, activity.mainLooper, null)
        Log.i(TAG, "Channel: $channel")

        val serviceInfo: WifiP2pServiceInfo = WifiP2pDnsSdServiceInfo.newInstance(
            QUICKDRAW_INSTANCE_NAME,
            QUICKDRAW_SERVICE_TYPE,
            mapOf() //TODO: game data to send to other devices
        )
        p2pManager.addLocalService(channel, serviceInfo, object: ActionListener {
            override fun onSuccess() {
                Log.i(TAG, "[PeerFinder] Successfully added Quickdraw service")
            }
            override fun onFailure(reason: Int) {
                Log.i(TAG, "[PeerFinder] there was a problem adding Quickdraw service: $reason")
            }
        })

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

    fun startScanning(){
        if(scanning)
            return
        Log.i(TAG, "[PeerFinder] Started scanning")
        explainPermissionsToUser()
        requestScanPermissions()

        ContextCompat.registerReceiver(
            activity,
            peerFinderBroadcastReceiver,
            PeerFinderBroadcastReceiver.getIntentFilter(),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        p2pManager.discoverServices(channel, object : ActionListener {
            override fun onSuccess() {
                Log.i(TAG, "Successfully started discovering peers")
                scanning = true
            }
            override fun onFailure(reason: Int) {
                Log.i(TAG, "There was an error discovering peers: $reason")
                scanning = false
            }
        })
    }

    fun stopScanning(){
        if(!scanning)
            return
        activity.unregisterReceiver(peerFinderBroadcastReceiver)
        p2pManager.stopPeerDiscovery(channel, object: ActionListener {
            override fun onSuccess() {
                Log.i(TAG, "[PeerFinder] Stopped scanning")
            }
            override fun onFailure(reason: Int) {
                Log.i(TAG, "[PeerFinder] Failed to stop SCANNING: $reason")
            }
        })
        p2pManager.removeGroup(channel, object: ActionListener {
            override fun onSuccess() { }
            override fun onFailure(reason: Int) { }
        })
    }

    fun startMatchWithPeer(peer: Peer) {
        Log.i(TAG, "[PeerFinder] Started match with peer $peer")
        val config = WifiP2pConfig()
        config.deviceAddress = peer.srcDevice.deviceAddress
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

    private fun explainPermissionsToUser(){
        //TODO
    }
    //Assume that this function is called when the user understands
    private fun requestScanPermissions(){
        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if(ContextCompat.checkSelfPermission(activity, Manifest.permission.NEARBY_WIFI_DEVICES) != PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.NEARBY_WIFI_DEVICES), 0)
            }
        }

        val locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gpsActive = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if(!gpsActive){
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            activity.startActivity(intent)
        }
    }

    private fun updatePeers() {
        val list: MutableList<Peer> = mutableListOf()
        for(dev in deviceToService.keys) {
            if(deviceToTxt.containsKey(dev)){
                //check that it's actually the quickdraw service
                if(deviceToService[dev]!!.first == QUICKDRAW_INSTANCE_NAME){
                    list.add(Peer(
                        dev,
                        instanceName = deviceToService[dev]!!.first,
                        registrationType = deviceToService[dev]!!.second,
                        fullDomainName = deviceToTxt[dev]!!.first,
                        txtRecordMap = deviceToTxt[dev]!!.second.toMap()
                    ))
                }
            }
        }
        peers = list
    }

    //Callbacks to receive data from android system
    //Called while discovering
    override fun onDnsSdTxtRecordAvailable(fullDomainName: String, txtRecordMap: MutableMap<String, String>, srcDevice: WifiP2pDevice) {
        deviceToTxt[srcDevice] = Pair(fullDomainName, txtRecordMap)
        Log.i(TAG, "[PeerServiceCallbacks] Txt: $fullDomainName $txtRecordMap $srcDevice")
        updatePeers()
    }

    override fun onDnsSdServiceAvailable(instanceName: String, registrationType: String, srcDevice: WifiP2pDevice) {
        deviceToService[srcDevice] = Pair(instanceName, registrationType)
        Log.i(TAG, "[PeerServiceCallbacks] Service: $instanceName $registrationType $srcDevice")
        updatePeers()
    }

    //Called while attempting to connect. Gets updated by the broadcast receiver
    override fun onConnectionInfoAvailable(info: WifiP2pInfo?) {
        Log.i(TAG, "[PeerServiceCallbacks] Connect info: $info")
        if(info == null || !info.groupFormed)
            return
        if(info.isGroupOwner){
            //Start server
        } else {
            //Start client
            val serverAddress = info.groupOwnerAddress
        }
    }

    companion object {
        const val QUICKDRAW_SERVICE_TYPE = "_quickdraw_tcp"
        const val QUICKDRAW_INSTANCE_NAME = "Quickdraw"
    }

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
