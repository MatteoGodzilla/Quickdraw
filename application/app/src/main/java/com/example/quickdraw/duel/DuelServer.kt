package com.example.quickdraw.duel

import android.util.Log
import com.example.quickdraw.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket

//Internal game server that handles the lower level communications
class DuelServer(private val receiver: MessageHandler) {
    private val outQueue: MutableList<Message> = mutableListOf()

    suspend fun startAsServer() = withContext(Dispatchers.IO){
        Log.i(TAG, "[DuelServer] Starting as server")
        val socket = ServerSocket(PORT)
        val client = socket.accept()
        Log.i(TAG, "[DuelServer] Accepted socket: $client")
        peerLoop(client)
    }

    suspend fun startAsClient(address: InetAddress) = withContext(Dispatchers.IO){
        Log.i(TAG, "[DuelServer] Starting as client")
        repeat(10){
            try{
                val server = Socket(address,PORT)
                Log.i(TAG, "[DuelServer] Connected to server")
                peerLoop(server)
            } catch (e: Exception){
                Log.e(TAG, e.message.toString())
            }
            delay(1000)
        }
    }

    suspend fun peerLoop(other: Socket) = withContext(Dispatchers.IO){
        receiver.onConnection(this@DuelServer)
        val incoming = BufferedReader(InputStreamReader(other.getInputStream()))
        val outgoing = PrintWriter(other.getOutputStream())
        while(!other.isClosed){
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

    companion object{
        private const val PORT = 54321
    }
}