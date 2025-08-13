package com.example.quickdraw.duel

import android.util.Log
import com.example.quickdraw.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.Socket

internal enum class DuelState {
    UNKNOWN,
    CAN_PLAY
}

class DuelGameLogic : MessageHandler{
    private var serverState = DuelState.UNKNOWN
        set(value) {
            field = value
            printStatus()
        }
    private var clientState = DuelState.UNKNOWN
        set(value) {
            field = value
            printStatus()
        }
    private var isServer = false
    private lateinit var duelServer: DuelServer
    
    override suspend fun onConnection(isServer: Boolean, duelServer: DuelServer) {
        this.isServer = isServer
        this.duelServer = duelServer
        if(isServer){
            duelServer.enqueueS2CMessage(Message(Type.HELLO))
        } else {
            duelServer.enqueueC2SMessage(Message(Type.HELLO))
        }
    }

    override suspend fun handleC2SMessage(message: Message, client: Socket) {
        when(message.type){
            Type.ACK -> {
                if(serverState == DuelState.UNKNOWN){
                    if(message.data == Type.HELLO.toString()){
                        serverState = DuelState.CAN_PLAY
                    }
                }
            }
            Type.HELLO -> {
                if(clientState == DuelState.UNKNOWN){
                    clientState = DuelState.CAN_PLAY
                    duelServer.enqueueS2CMessage(Message(Type.ACK, message.type.toString()))
                }
            }
            Type.READY -> TODO()
            Type.STEADY -> TODO()
            Type.BANG -> TODO()
        }
    }

    override suspend fun handleS2CMessage(message: Message, server: Socket) {
        when(message.type){
            Type.ACK -> {
                if(clientState == DuelState.UNKNOWN) {
                    if(message.data == Type.HELLO.toString()){
                        clientState = DuelState.CAN_PLAY
                    }
                }
            }
            Type.HELLO -> {
                if(serverState == DuelState.UNKNOWN){
                    serverState = DuelState.CAN_PLAY
                    duelServer.enqueueC2SMessage(Message(Type.ACK, message.type.toString()))
                }
            }
            Type.READY -> TODO()
            Type.STEADY -> TODO()
            Type.BANG -> TODO()
        }
    }

    //STATE CHANGERS

    suspend fun ready() = withContext(Dispatchers.IO){
        if(clientState == DuelState.CAN_PLAY && serverState == DuelState.CAN_PLAY){
            Log.i(TAG, "Poggers")
        }
    }

    private fun printStatus(){
        Log.i(TAG, "S:$serverState\tC:$clientState")
    }
}