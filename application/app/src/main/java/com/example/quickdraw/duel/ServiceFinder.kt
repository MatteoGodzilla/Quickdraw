package com.example.quickdraw.duel

import android.annotation.SuppressLint
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pManager.ActionListener
import android.net.wifi.p2p.WifiP2pManager.Channel
import android.net.wifi.p2p.WifiP2pManager.DnsSdServiceResponseListener
import android.net.wifi.p2p.WifiP2pManager.DnsSdTxtRecordListener
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest
import android.util.Log
import com.example.quickdraw.TAG
import com.example.quickdraw.duel.Peer.Companion.BOUNTY_KEY
import com.example.quickdraw.duel.Peer.Companion.HEALTH_KEY
import com.example.quickdraw.duel.Peer.Companion.ID_KEY
import com.example.quickdraw.duel.Peer.Companion.LEVEL_KEY
import com.example.quickdraw.duel.Peer.Companion.MAX_HEALTH_KEY
import com.example.quickdraw.duel.Peer.Companion.USERNAME_KEY
import kotlinx.coroutines.flow.MutableStateFlow


//Class responsible for finding other players nearby
@SuppressLint("MissingPermission")
class ServiceFinder (
    private val channel:Channel,
    private val manager:WifiP2pManager,
) : DnsSdTxtRecordListener, DnsSdServiceResponseListener {

    companion object {
        const val QUICKDRAW_SERVICE_TYPE = "_quickdraw_tcp"
        const val QUICKDRAW_INSTANCE_NAME = "Quickdraw"
    }


    //vvvvv should access this from outside
    val rawDeviceToPeer: MutableStateFlow<Map<String, Peer>> = MutableStateFlow(mapOf())
    //utility collections
    private var rawDevicesWithService: MutableList<String> = mutableListOf()
    private var rawDevicesToMap: MutableMap<String, Map<String, String>> = mutableMapOf()
    private var serviceInfo: WifiP2pDnsSdServiceInfo? = null

    fun startDiscover(playerInfo: Peer){
        serviceInfo = WifiP2pDnsSdServiceInfo.newInstance(
            QUICKDRAW_INSTANCE_NAME,
            QUICKDRAW_SERVICE_TYPE,
            playerInfo.getValuesAsMap()
        )

        manager.addLocalService(channel, serviceInfo, object : ActionListener {
            override fun onSuccess() {
                Log.i(TAG, "[PeerFinder] Service registered: $QUICKDRAW_SERVICE_TYPE")
            }
            override fun onFailure(reason: Int) {
                Log.e(TAG, "[PeerFinder] Failed to register service: $reason")
            }
        })

        manager.setDnsSdResponseListeners(channel, this, this)
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
    }

    fun stopDiscover(){
        manager.stopPeerDiscovery(channel, object : ActionListener {
            override fun onSuccess() {
                Log.i(TAG, "[ServiceFinder] Stopped discovering service devices")
                if(serviceInfo != null){
                    manager.removeLocalService(channel, serviceInfo!!, object : ActionListener {
                        override fun onSuccess() {
                            Log.i(TAG, "[ServiceFinder] Successfully removed local service")
                        }

                        override fun onFailure(reason: Int) {
                            Log.i(TAG, "[ServiceFinder] There was a problem removing local service: $reason")
                        }
                    })
                }
            }

            override fun onFailure(reason: Int) {
                Log.i(TAG, "[ServiceFinder] There was a problem stopping discovering")
            }
        })
    }

    private fun updatePeers() {
        val device2PeerMap = mutableMapOf<String, Peer>()
        for(dev in rawDevicesWithService){
            if(rawDevicesToMap.containsKey(dev)){
                //Deserialize map into Peer object
                val dictionary = rawDevicesToMap[dev]
                val id: Int = dictionary?.get(ID_KEY)?.toInt() ?: 0
                val username: String = dictionary?.get(USERNAME_KEY) ?: ""
                val level = dictionary?.get(LEVEL_KEY)?.toInt() ?: 0
                val health = dictionary?.get(HEALTH_KEY)?.toInt() ?: 100
                val maxHealth = dictionary?.get(MAX_HEALTH_KEY)?.toInt() ?: 100
                val bounty = dictionary?.get(BOUNTY_KEY)?.toInt() ?: 0
                val peer = Peer(id, username, level, health, maxHealth, bounty)
                device2PeerMap[dev] = peer
            }
        }
        Log.i(TAG,"[PeerFinder] Updating Peers")
        for(p in device2PeerMap){
            Log.i(TAG,p.toString())
        }
        Log.i(TAG,"-----------")
        rawDeviceToPeer.value = device2PeerMap
    }


    //Callbacks to receive data from android system
    //Called while discovering
    override fun onDnsSdTxtRecordAvailable(fullDomainName: String, txtRecordMap: MutableMap<String, String>, srcDevice: WifiP2pDevice) {
        rawDevicesToMap[srcDevice.deviceAddress] = txtRecordMap
        Log.i(TAG, "[PeerServiceCallbacks] Txt: $fullDomainName $txtRecordMap $srcDevice")
        updatePeers()
    }

    override fun onDnsSdServiceAvailable(instanceName: String, registrationType: String, srcDevice: WifiP2pDevice) {
        rawDevicesWithService.add(srcDevice.deviceAddress)
        updatePeers()
    }
}
