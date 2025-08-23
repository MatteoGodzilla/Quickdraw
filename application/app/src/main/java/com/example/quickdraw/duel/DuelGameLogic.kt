package com.example.quickdraw.duel

import android.util.Log
import com.example.quickdraw.TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.Dispatcher
import java.net.Socket
import kotlin.random.Random

enum class DuelState {
    UNKNOWN,
    CAN_PLAY,
    READY,
    STEADY,
    BANG
}

//Higher level logic for actually handling the game part
class DuelGameLogic(private var peer: Peer) : MessageHandler{
    val selfState = MutableStateFlow(DuelState.UNKNOWN)
    val peerState = MutableStateFlow(DuelState.UNKNOWN)
    var otherPeer = MutableStateFlow(Peer("", 1, 100, 100))


    //deciding when players should shoot
    private var selfChosenDelay = 0.0
    private var peerChosenDelay = 0.0
    var agreedBangDelay = 0.0
        private set
    private var damageToPeer: Int = 0
    //Time delta when players actually shot
    private var selfBangDelta = 0.0
    private var peerBangDelta = 0.0
    //Reference
    var referenceTimeMS = 0L
        private set


    private lateinit var duelServer: DuelServer
    private val localScope = CoroutineScope(Dispatchers.IO)

    override suspend fun onConnection(duelServer: DuelServer) {
        this.duelServer = duelServer
        duelServer.enqueueOutgoing(Message(Type.SETUP, Json.encodeToString(peer)))
    }

    override suspend fun handleIncoming(message: Message, other: Socket) {
        when(message.type){
            Type.ACK -> {
                if(selfState.value == DuelState.UNKNOWN){
                    if(message.data == Type.SETUP.toString()){
                        selfState.value = DuelState.CAN_PLAY
                        printStatus()
                    } //otherwise it should throw something or error
                }
            }
            Type.SETUP -> {
                if(peerState.value == DuelState.UNKNOWN){
                    peerState.value = DuelState.CAN_PLAY
                    otherPeer.value = Json.decodeFromString(message.data)
                    printStatus()
                    duelServer.enqueueOutgoing(Message(Type.ACK, message.type.toString()))
                }
            }
            Type.READY -> {
                if(peerState.value == DuelState.CAN_PLAY) {
                    peerState.value = DuelState.READY
                    printStatus()
                    checkReady()
                }
            }
            Type.STEADY -> {
                peerChosenDelay = message.data.toDouble()
                peerState.value = DuelState.STEADY
                printStatus()
                checkSteady()
            }
            Type.BANG -> {
                //Get time from peer and check
                peerState.value = DuelState.BANG
                printStatus()
                peerBangDelta = message.data.toDouble()
                checkBang()
            }
            Type.DAMAGE -> {
                //apply damage from opponent if self has lost the round
                if(!didSelfWin()) {
                    val damageToSelf = message.data.toInt()
                    Log.i(TAG, "Accepting damage: $damageToSelf")
                } else {
                    Log.i(TAG, "There is DEFINETLY SOMETHING WRONG")
                }
            }
            Type.RESET -> {
                peerState.value = DuelState.CAN_PLAY
                printStatus()
            }
        }
    }

    //UI Functions

    fun setReady(damageToPeer: Int) = localScope.launch{
        selfState.value = DuelState.READY
        printStatus()
        this@DuelGameLogic.damageToPeer = damageToPeer
        duelServer.enqueueOutgoing(Message(Type.READY))
        checkReady()
    }

     fun bang() = localScope.launch{
        selfState.value = DuelState.BANG
        printStatus()
        selfBangDelta = System.currentTimeMillis() - referenceTimeMS - agreedBangDelay
        duelServer.enqueueOutgoing(Message(Type.BANG, selfBangDelta.toString()))
        checkBang()
    }

    fun nextRound() = localScope.launch{
        selfState.value = DuelState.CAN_PLAY
        printStatus()
        duelServer.enqueueOutgoing(Message(Type.RESET))
    }

    //Stuff to do when both peers are in the same state
    private suspend fun checkReady(){
        if(selfState.value == DuelState.READY && peerState.value == DuelState.READY) {
            selfState.value = DuelState.STEADY
            printStatus()
            selfChosenDelay = Random.nextDouble(5000.0,10000.0) //milliseconds
            duelServer.enqueueOutgoing(Message(Type.STEADY, selfChosenDelay.toString()))
            checkSteady()
        }
    }

    private fun checkSteady(){
        if(selfState.value == DuelState.STEADY && peerState.value == DuelState.STEADY){
            //set reference time
            referenceTimeMS = System.currentTimeMillis()
            agreedBangDelay = (selfChosenDelay + peerChosenDelay) / 2
            Log.i(TAG, "[] Agreed on delay $agreedBangDelay")
        }
    }

    private suspend fun checkBang() {
        if(selfState.value == DuelState.BANG && peerState.value == DuelState.BANG) {
            //check who won
            Log.i(TAG, "CHECKING $selfBangDelta $peerBangDelta")
            //Option 1: to win the delay must be positive, but the smallest
            //Option 2: closest to target wins, even if it's early

            if(didSelfWin()) duelServer.enqueueOutgoing(Message(Type.DAMAGE, damageToPeer.toString()))
        }
    }

    //Option 1
    private fun didSelfWin() = selfBangDelta > 0 && if(peerBangDelta > 0) selfBangDelta < peerBangDelta else true

    private fun printStatus(){
        Log.i(TAG, "S:${selfState.value}\tP:${peerState.value}")
    }

}