package com.example.quickdraw.game.vm

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pDevice
import android.os.Build
import android.provider.Settings
import android.provider.Settings.ACTION_WIFI_SETTINGS
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.example.quickdraw.ImageLoader
import com.example.quickdraw.duel.Peer
import com.example.quickdraw.duel.PeerFinder
import com.example.quickdraw.duel.ServiceFinder
import com.example.quickdraw.game.ManualConnectionActivity
import com.example.quickdraw.game.PermissionBroadcastReceiver
import com.example.quickdraw.game.repo.GameRepository
import com.example.quickdraw.game.screen.FightableEntity
import kotlinx.coroutines.flow.MutableStateFlow

data class FightablePeer(val rawDevice: WifiP2pDevice, val playerInfo: Peer? = null)

class MainScreenVM(
    private val repository: GameRepository,
    val peerFinder: PeerFinder,
    private val context: Activity,
    val imageLoader: ImageLoader,
    val serviceFinder: ServiceFinder,
    pbr: PermissionBroadcastReceiver,
) : ViewModel() {

    val player = repository.player.player
    val stats = repository.player.stats
    val bandits = repository.bandits.bandits
    val peers = MutableStateFlow<List<FightablePeer>>(listOf())
    val scanning = peerFinder.scanning

    //Permission stuff
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
            serviceFinder.stopDiscover()
        } else {
            peerFinder.startScanning(context)
            serviceFinder.startDiscover(repository.getPlayerAsPeer())
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

    fun getPeers(){
        peers.value = peerFinder.rawDevices.value.map { rawPeer ->
            FightablePeer(rawPeer, serviceFinder.rawDeviceToPeer.value[rawPeer.deviceAddress])
        }
    }

    fun startMatchWithPeer(peer: FightablePeer) = peerFinder.startMatchWithPeer(peer.rawDevice)
    fun checkInventoryForWeapon() = repository.inventory.checkInventoryForWeapon()
    fun checkInventoryForShoot() = repository.inventory.checkInventoryForShoot()

    fun levelProgress() = repository.player.getProgressToNextLevel()
}