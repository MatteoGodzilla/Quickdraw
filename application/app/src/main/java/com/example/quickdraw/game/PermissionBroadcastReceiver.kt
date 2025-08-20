package com.example.quickdraw.game

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.util.Log
import com.example.quickdraw.TAG
import kotlinx.coroutines.flow.MutableStateFlow


class PermissionBroadcastReceiver(context: Context) : BroadcastReceiver() {
    private val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val wifiActive = MutableStateFlow(wifiManager.isWifiEnabled)
    val gpsActive = MutableStateFlow(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))


    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent != null) {
            val action = intent.action

            if (action == WifiManager.NETWORK_STATE_CHANGED_ACTION) {
                wifiActive.value = wifiManager.isWifiEnabled
            } else if(action == LocationManager.PROVIDERS_CHANGED_ACTION){
                Log.i(TAG, "ASDFASDF AUBCAWTCBUOTCWOBUCWTBUOCATWNOVYUNTLMCFNUTE%J")
                gpsActive.value = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            }
        }
    }

    companion object{
        fun getIntentFilter(): IntentFilter {
            return IntentFilter().apply {
                 addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
                 addAction(LocationManager.PROVIDERS_CHANGED_ACTION)
            }
        }
    }
}