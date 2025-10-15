package com.example.quickdraw.duel

import android.util.Log
import com.example.quickdraw.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket

//Internal game server that handles the lower level communications
class DuelServer(private val receiver: MessageHandler) {
    private val outQueue: MutableList<Message> = mutableListOf()
    private var serverSocket : ServerSocket? = null
    private var clientSocket : Socket? = null
    private var doneReceiving = false

    suspend fun startAsServer(port: Int) = withContext(Dispatchers.IO){
        if(serverSocket != null)
            return@withContext
        Log.i(TAG, "[DuelServer] Starting as server")
        serverSocket = ServerSocket()
        serverSocket!!.reuseAddress = true
        serverSocket!!.bind(InetSocketAddress(port))
        val client = serverSocket!!.accept()
        Log.i(TAG, "[DuelServer] Accepted socket: $client")
        peerLoop(client)
        Log.i(TAG, "[DuelServer] Closing socket: $client")
        client.close()
        serverSocket!!.close()
    }

    suspend fun startAsClient(address: InetAddress, port: Int) = withContext(Dispatchers.IO){
        if(clientSocket != null)
            return@withContext
        Log.i(TAG, "[DuelServer] Starting as client")
        clientSocket = Socket(address, port)
        Log.i(TAG, "[DuelServer] Connected to server")
        peerLoop(clientSocket!!)
        Log.i(TAG, "[DuelServer] Closing socket: $clientSocket")
        clientSocket!!.close()
    }

    suspend fun peerLoop(other: Socket) = withContext(Dispatchers.IO){
        receiver.onConnection(this@DuelServer)
        val incoming = BufferedReader(InputStreamReader(other.getInputStream()))
        val outgoing = PrintWriter(other.getOutputStream())
        while(!other.isClosed && !doneReceiving){
            if(other.getInputStream().available() > 0){
                //read data from other
                val line = incoming.readLine()
                val message = Message.deserialize(line)
                Log.i(TAG, "<- : $message")
                receiver.handleIncoming(message, other)
            }
            if(outQueue.isNotEmpty()){
                //send data to other
                val top = outQueue.first()
                outQueue.removeAt(0)
                Log.i(TAG," ->: $top")
                outgoing.println(top.serialize())
                outgoing.flush()
            }
        }
    }

    suspend fun enqueueOutgoing(message:Message) = withContext(Dispatchers.IO){
        outQueue.add(message)
    }

    suspend fun doneReceiving() = withContext(Dispatchers.IO){
        doneReceiving = true
    }

    companion object{
        const val PORT = 54321
    }
}