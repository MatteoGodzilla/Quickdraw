package com.example.quickdraw.game.vm

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.provider.Settings.ACTION_WIFI_SETTINGS
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.example.quickdraw.ImageLoader
import com.example.quickdraw.duel.Peer
import com.example.quickdraw.duel.Peer2
import com.example.quickdraw.duel.PeerFinder
import com.example.quickdraw.game.ManualConnectionActivity
import com.example.quickdraw.game.PermissionBroadcastReceiver
import com.example.quickdraw.game.repo.GameRepository

class MainScreenVM(
    private val repository: GameRepository,
    private val peerFinder: PeerFinder,
    private val context: Activity,
    val imageLoader: ImageLoader,
    pbr: PermissionBroadcastReceiver,
) : ViewModel() {

    val player = repository.player.player
    val stats = repository.player.stats
    val bandits = repository.bandits.bandits
    val peers = peerFinder.peers
    val scanning = peerFinder.scanning

    val expandedChecks = mutableStateOf(false)

    var permFineLocation = false
    var permNearbyDevices = false
    var wifiP2PActive = false
    //handled by pbr
    var wifiActive = pbr.wifiActive
    var gpsActive = pbr.gpsActive

    fun checkValidScan(): Boolean {
        permFineLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED

        permNearbyDevices = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.NEARBY_WIFI_DEVICES) == PERMISSION_GRANTED
        } else {
            true
        }

        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiP2PActive = wifiManager.isP2pSupported

        return permFineLocation && permNearbyDevices && wifiActive.value && gpsActive.value
    }

    fun onScan() {
        if(peerFinder.scanning.value){
            peerFinder.stopScanning()
        } else {
            val self = repository.player.player.value
            val stats = repository.player.stats.value
            //peerFinder.startScanning(Peer(self.id, self.username, self.level, self.health, stats.maxHealth,self.bounty), context)
            peerFinder.startScanning(context)
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
        catch(_: ActivityNotFoundException){
            val i = Intent(ACTION_WIFI_SETTINGS)
            context.startActivity(i)
        }
    }

    fun onLocationSettings(){
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        context.startActivity(intent)
    }

    fun goToManualMatch(){
        peerFinder.stopScanning()
        val intent = Intent(context, ManualConnectionActivity::class.java)
        context.startActivity(intent)
    }

    fun startMatchWithPeer(peer: Peer2) = peerFinder.startMatchWithPeer(peer)
    fun checkInventoryForWeapon() = repository.inventory.checkInventoryForWeapon()
    fun checkInventoryForShoot() = repository.inventory.checkInventoryForShoot()

    fun levelProgress() = repository.player.getProgressToNextLevel()
}