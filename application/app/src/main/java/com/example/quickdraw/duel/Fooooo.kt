package com.example.quickdraw.duel

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager

interface FooCallbacks {
    fun onDiscoveryStatusChange(status: Int)
    fun onChangePeers(peers: WifiP2pDeviceList)
    fun onConnectionListener(connectionInfo: WifiP2pInfo?)
}

class Fooooo(private val p2pManager: WifiP2pManager, private val channel: WifiP2pManager.Channel, private val callbacks: FooCallbacks) : BroadcastReceiver() {
    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context?, intent: Intent) {
        when(intent.action){
            WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION -> {
                val status = intent.getIntExtra(WifiP2pManager.EXTRA_DISCOVERY_STATE, -1)
                callbacks.onDiscoveryStatusChange(status)
            }
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                p2pManager.requestPeers(channel, callbacks::onChangePeers)
            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                p2pManager.requestConnectionInfo(channel, callbacks::onConnectionListener)
            }
        }
    }



}