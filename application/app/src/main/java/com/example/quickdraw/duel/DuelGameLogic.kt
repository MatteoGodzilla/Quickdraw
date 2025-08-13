package com.example.quickdraw.duel

import android.util.Log
import com.example.quickdraw.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.net.Socket
import kotlin.random.Random

internal enum class DuelState {
    UNKNOWN,
    CAN_PLAY,
    READY,
    STEADY,
    BANG,
    RESULTS
}

//Higher level logic for actually handling the game part
class DuelGameLogic : MessageHandler{
    private var selfState = DuelState.UNKNOWN
        set(value) {
            field = value
            printStatus()
        }
    private var peerState = DuelState.UNKNOWN
        set(value) {
            field = value
            printStatus()
        }

    private var selfDelay = 0.0
    private var peerDelay = 0.0
    private var bangTargetDelay = 0.0
    private var selfBangDelay = 0.0
    private var peerBangDelay = 0.0

    private lateinit var duelServer: DuelServer

    override suspend fun onConnection(duelServer: DuelServer) {
        this.duelServer = duelServer
        duelServer.enqueueOutgoing(Message(Type.HELLO))
    }

    override suspend fun handleIncoming(message: Message, other: Socket) {
        when(message.type){
            Type.ACK -> {
                if(peerState == DuelState.UNKNOWN){
                    if(message.data == Type.HELLO.toString()){
                        selfState = DuelState.CAN_PLAY
                    } //otherwise it should throw something or error
                }
            }
            Type.HELLO -> {
                if(peerState == DuelState.UNKNOWN){
                    peerState = DuelState.CAN_PLAY
                    duelServer.enqueueOutgoing(Message(Type.ACK, message.type.toString()))
                }
            }
            Type.READY -> {
                if(peerState == DuelState.CAN_PLAY) {
                    peerState = DuelState.READY
                    checkReady()
                }
            }
            Type.STEADY -> {
                peerDelay = message.data.toDouble()
                peerState = DuelState.STEADY
                checkSteady()
            }
            Type.BANG -> TODO()
        }
    }

    suspend fun setReady() {
        selfState = DuelState.READY
        duelServer.enqueueOutgoing(Message(Type.READY))
        checkReady()
    }


    suspend fun bang(){
        selfState = DuelState.BANG
        //get delta time
        val delta = 0.1
        duelServer.enqueueOutgoing(Message(Type.BANG, delta.toString()))
    }

    //Stuff to do when both peers are in the same state
    suspend fun checkReady(){
        if(selfState == DuelState.READY && peerState == DuelState.READY) {
            selfState = DuelState.STEADY
            //generate random number
            selfDelay = Random.nextDouble(5.0,10.0) //seconds
            duelServer.enqueueOutgoing(Message(Type.STEADY, selfDelay.toString()))
            checkSteady()
        }
    }

    suspend fun checkSteady(){
        if(selfState == DuelState.STEADY && peerState == DuelState.STEADY){
            //set reference time
            bangTargetDelay = (selfDelay + peerDelay) / 2

        }
    }

    suspend fun checkBang() {
        if(selfState == DuelState.BANG && peerState == DuelState.BANG) {
            //check who won
            //Option 1: to win the delay must be positive, but the smallest
            //Option 2: to win the absolute value of the delay must be smallest

        }
    }

    private fun printStatus(){
        Log.i(TAG, "S:$selfState\tP:$peerState")
    }

}