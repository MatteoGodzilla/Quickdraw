package com.example.quickdraw.game.vm

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.provider.Settings.ACTION_WIFI_SETTINGS
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.example.quickdraw.duel.Peer
import com.example.quickdraw.duel.PeerFinder
import com.example.quickdraw.game.PermissionBroadcastReceiver
import com.example.quickdraw.game.repo.GameRepository

class MainScreenVM(
    private val repository: GameRepository,
    private val peerFinder: PeerFinder,
    private val context: Activity,
    pbr: PermissionBroadcastReceiver
) : ViewModel() {

    val player = repository.player.player
    val stats = repository.player.stats

    val peers = peerFinder.peers
    val scanning = peerFinder.scanning

    var permFineLocation = false
    var permNearbyDevices = false
    var wifiP2PActive = false
    //handled by pbr
    var wifiActive = pbr.wifiActive
    var gpsActive = pbr.gpsActive

    fun checkValidScan(): Boolean {
        permFineLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permNearbyDevices = ContextCompat.checkSelfPermission(context, Manifest.permission.NEARBY_WIFI_DEVICES) == PERMISSION_GRANTED
        } else {
            permNearbyDevices = true
        }

        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiP2PActive = wifiManager.isP2pSupported

        return permFineLocation && permNearbyDevices && wifiActive.value && gpsActive.value
    }

    fun onScan() {
        if(peerFinder.scanning.value){
            peerFinder.stopScanning()
        } else {
            peerFinder.startScanning(Peer(
                repository.player.player.value.username,
                repository.player.player.value.level
            ), context)
        }
    }

    fun onWifiSettings(){
        val intent = Intent(ACTION_WIFI_SETTINGS)
        context.startActivity(intent)
    }

    fun onWifiP2PSettings() {
        try{
            val i = Intent().apply {
                component = ComponentName(
                    "com.android.settings",
                    "com.android.settings.wifi.p2p.WifiP2pSettings"
                )
            }
            context.startActivity(i)
        }
        catch(e: ActivityNotFoundException){
            val i = Intent(ACTION_WIFI_SETTINGS)
            context.startActivity(i)
        }
    }

    fun onLocationSettings(){
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        context.startActivity(intent)
    }

    fun startMatchWithPeer(peer: Peer) = peerFinder.startMatchWithPeer(peer)

    fun levelProgress() = repository.player.getProgressToNextLevel()
}