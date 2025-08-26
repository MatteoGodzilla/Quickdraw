package com.example.quickdraw.game.vm

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickdraw.TAG
import com.example.quickdraw.duel.Peer
import com.example.quickdraw.game.repo.GameRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okio.IOException
import java.net.Inet4Address
import java.net.ServerSocket
import java.net.Socket

class ManualConnectionVM(
    private val repository: GameRepository,
    private val context: Context,
    private val onConnection: (isServer:Boolean, serverAddress:String)->Unit
) : ViewModel() {
    val scanning = MutableStateFlow(false)
    val messageError = MutableStateFlow("")
    private var server: ServerSocket? = null
    private var alreadyStarted = false

    init {
        listenAsServer()
    }

    fun startScanning(){
        closeServer()
        scanning.value = true
    }

    fun closeServer(){
        server?.close()
    }

    fun onScan(value:String) = viewModelScope.launch(Dispatchers.IO){
        if(alreadyStarted)
            return@launch
        alreadyStarted = true
        try {
            val data = Json.decodeFromString<ManualClientConnection>(value)
            val otherSocket = Socket(data.address, DISCOVER_PORT)
            if(otherSocket.isConnected && scanning.value){
                onConnection(false, data.address)
            }
            otherSocket.close()
        } catch (e: Exception){
            Log.e(TAG, "INVALID DATA INPUT FROM SCANNED QR: $e")
            if(e is java.net.NoRouteToHostException){
                messageError.update{"Host phone is not reachable,are you sure to be on the same wifi?"}
            }
            else{
                messageError.update{"Some error occured,please try again"}
            }
        }
        scanning.update { false }
    }

    private fun listenAsServer() : Job = viewModelScope.launch(Dispatchers.IO) {
        try{
            server = ServerSocket(DISCOVER_PORT)
            Log.i(TAG, "Started listening for a manual match")
            val otherPeer = server!!.accept()
            if(otherPeer.isConnected && !scanning.value){
                onConnection(true,"")
            }
            otherPeer.close()
        } catch(e: IOException){
            Log.i(TAG, "Listening server was forced close, because we started scanning")
        }
    }

    fun getQRData(): String{
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val address = connectivityManager.getLinkProperties(connectivityManager.activeNetwork)?.linkAddresses!!.first { x->x.address is Inet4Address }.toString().split("/")[0]
        return Json.encodeToString(ManualClientConnection(address, getSelfAsPeer()))
    }

    private fun getSelfAsPeer(): Peer {
        val player = repository.player.player.value
        val stats = repository.player.stats.value
        return Peer(player.id, player.username, player.level, player.health, stats.maxHealth)
    }

    companion object{
        const val DISCOVER_PORT = 54320
    }

    @Serializable
    data class ManualClientConnection(
        val address: String,
        val self: Peer
    )
}