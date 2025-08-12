package com.example.quickdraw.duel

import android.util.Log
import com.example.quickdraw.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket

internal enum class Type {
    READY,
    STEADY,
    BANG
}
internal data class Message (val type: Type, val data: String){
    companion object {
        fun deserialize(s: String) : Message{
            val tokens = s.split(':')
            val type = tokens[0].toInt()
            return Message(Type.entries[type], tokens[1])
        }
    }
    fun serialize(): String{
        return "${type.ordinal}:${data}"
    }
}

//Actual game server that handles the comms for rounds and the general duel
class DuelServer {
    var isServer = false

    private val c2sQueue: MutableList<Message> = mutableListOf()
    private val s2cQueue: MutableList<Message> = mutableListOf()

    suspend fun startAsServer() = withContext(Dispatchers.IO){
        Log.i(TAG, "[DuelServer] Starting as server")
        isServer = true
        val socket = ServerSocket(PORT)
        val client = socket.accept()
        Log.i(TAG, "[DuelServer] Accepted socket: $client")
        val c2s = BufferedReader(InputStreamReader(client.getInputStream()))
        val s2c = PrintWriter(client.getOutputStream())
        while(!client.isClosed){
            if(client.getInputStream().available() > 0){
                //read data from server
                val line = c2s.readLine()
                val message = Message.deserialize(line)
                //Assume it's a message
                Log.i(TAG, "C2S: $message")
            }
            if(s2cQueue.isNotEmpty()){
                //send data to server
                val top = s2cQueue.first()
                s2cQueue.removeAt(0)
                s2c.println(top.serialize())
                s2c.flush()
            }
        }
    }

    suspend fun startAsClient(address: InetAddress) = withContext(Dispatchers.IO){
        Log.i(TAG, "[DuelServer] Starting as client")
        isServer = false
        repeat(10){
            try{
                val server = Socket(address,PORT)
                val s2c = BufferedReader(InputStreamReader(server.getInputStream()))
                val c2s = PrintWriter(server.getOutputStream())
                Log.i(TAG, "[DuelServer] Connected to server")
                while(server.isConnected){
                    if(server.getInputStream().available() > 0){
                        //read data from server
                        val line = s2c.readLine()
                        val message = Message.deserialize(line)
                        Log.i(TAG, "S2C: $message")
                    }
                    if(c2sQueue.isNotEmpty()){
                        //send data to server
                        val top = c2sQueue.first()
                        c2sQueue.removeAt(0)

                        c2s.println(top.serialize())
                        c2s.flush()
                    }
                }
                server.close()
            } catch (e: Exception){
                Log.e(TAG, e.message.toString())
            }
            delay(1000)
        }
    }

    suspend fun ready() = withContext(Dispatchers.IO){
        if(isServer){
            s2cQueue.add(Message(Type.READY, "Ready"))
        } else {
            c2sQueue.add(Message(Type.STEADY, "Steady"))
        }
    }

    companion object{
        const val PORT = 54321
    }
}