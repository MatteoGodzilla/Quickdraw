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

//Actual game server that handles the comms for rounds and the general duel
class DuelServer(private val receiver: MessageHandler) {

    private val c2sQueue: MutableList<Message> = mutableListOf()
    private val s2cQueue: MutableList<Message> = mutableListOf()

    suspend fun startAsServer() = withContext(Dispatchers.IO){
        Log.i(TAG, "[DuelServer] Starting as server")
        val socket = ServerSocket(PORT)
        val client = socket.accept()
        Log.i(TAG, "[DuelServer] Accepted socket: $client")
        val c2s = BufferedReader(InputStreamReader(client.getInputStream()))
        val s2c = PrintWriter(client.getOutputStream())
        receiver.onConnection(true, this@DuelServer)
        while(!client.isClosed){
            if(client.getInputStream().available() > 0){
                //read data from server
                val line = c2s.readLine()
                val message = Message.deserialize(line)
                Log.i(TAG, "C->S: $message")
                receiver.handleC2SMessage(message, client)
            }
            if(s2cQueue.isNotEmpty()){
                //send data to server
                val top = s2cQueue.first()
                s2cQueue.removeAt(0)
                Log.i(TAG,"S->C: $top")
                s2c.println(top.serialize())
                s2c.flush()
            }
        }
    }

    suspend fun startAsClient(address: InetAddress) = withContext(Dispatchers.IO){
        Log.i(TAG, "[DuelServer] Starting as client")
        repeat(10){
            try{
                val server = Socket(address,PORT)
                val s2c = BufferedReader(InputStreamReader(server.getInputStream()))
                val c2s = PrintWriter(server.getOutputStream())
                Log.i(TAG, "[DuelServer] Connected to server")
                receiver.onConnection(false, this@DuelServer)
                while(server.isConnected){
                    if(server.getInputStream().available() > 0){
                        //read data from server
                        val line = s2c.readLine()
                        val message = Message.deserialize(line)
                        Log.i(TAG, "S->C: $message")
                        receiver.handleS2CMessage(message, server)
                    }
                    if(c2sQueue.isNotEmpty()){
                        //send data to server
                        val top = c2sQueue.first()
                        c2sQueue.removeAt(0)
                        Log.i(TAG, "C->S: $top")
                        c2s.println(top.serialize())
                        c2s.flush()
                    }
                }
            } catch (e: Exception){
                Log.e(TAG, e.message.toString())
            }
            delay(1000)
        }
    }

    suspend fun enqueueS2CMessage(message: Message) = withContext(Dispatchers.IO){
        s2cQueue.add(message)
    }

    suspend fun enqueueC2SMessage(message: Message) = withContext(Dispatchers.IO){
        c2sQueue.add(message)
    }

    companion object{
        private const val PORT = 54321
    }
}