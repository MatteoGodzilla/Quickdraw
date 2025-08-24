package com.example.quickdraw.duel

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import com.example.quickdraw.TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.net.Socket
import kotlin.random.Random

enum class DuelState {
    UNKNOWN,
    CAN_PLAY,
    READY,
    STEADY,
    BANG,
    DONE
}

//Higher level logic for actually handling the game part
class DuelGameLogic(
    peer: Peer,
    private val rounds: Int,
    private val context: Context
) : MessageHandler{
    val selfState = MutableStateFlow(DuelState.UNKNOWN)
    val peerState = MutableStateFlow(DuelState.UNKNOWN)
    val selfPeer = MutableStateFlow(peer)
    val otherPeer = MutableStateFlow(Peer("", 1, 100, 100))
    val shouldShoot = MutableStateFlow(false)
    val currentRound = MutableStateFlow(0)

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


    private val MIN_DELAY = 5000.0
    private val MAX_DELAY = 10000.0

    private lateinit var duelServer: DuelServer
    private val localScope = CoroutineScope(Dispatchers.IO)

    override suspend fun onConnection(duelServer: DuelServer) {
        this.duelServer = duelServer
        duelServer.enqueueOutgoing(Message(Type.SETUP, Json.encodeToString(selfPeer.value)))
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
                    val damageReceived = message.data.toInt()
                    Log.i(TAG, "Accepting damage: $damageReceived")
                    selfPeer.value = selfPeer.value.copy(health = selfPeer.value.health - damageReceived)
                    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                    vibrator.vibrate(VibrationEffect.createOneShot(1500, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    Log.i(TAG, "There is DEFINETLY SOMETHING WRONG")
                }
            }
            Type.NEW_ROUND -> {
                peerState.value = DuelState.CAN_PLAY
                printStatus()
            }
            Type.DONE -> {
                peerState.value = DuelState.DONE
                printStatus()
            }
        }
    }

    //UI Functions

    fun setReady(damageToPeer: Int) = localScope.launch{
        selfState.value = DuelState.READY
        shouldShoot.value = false
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
        if(canGoToNextRound()){
            selfState.value = DuelState.CAN_PLAY
            printStatus()
            duelServer.enqueueOutgoing(Message(Type.NEW_ROUND))
            currentRound.value++
        } else {
            selfState.value = DuelState.DONE
            printStatus()
            duelServer.enqueueOutgoing(Message(Type.DONE))
        }
    }

    private fun startPolling() = localScope.launch{
        delay(MIN_DELAY.toLong() / 2)
        while(selfState.value == DuelState.STEADY)  {
            val delta = System.currentTimeMillis() - referenceTimeMS - agreedBangDelay
            if(delta > 0){
                //start vibrating
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
                shouldShoot.value = true
                break;
            }
        }
    }
    fun canGoToNextRound() = currentRound.value + 1 < rounds

    //Stuff to do when both peers are in the same state
    private suspend fun checkReady(){
        if(selfState.value == DuelState.READY && peerState.value == DuelState.READY) {
            selfState.value = DuelState.STEADY
            printStatus()
            selfChosenDelay = Random.nextDouble(MIN_DELAY,MAX_DELAY) //milliseconds
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
            startPolling()
        }
    }

    private suspend fun checkBang() {
        if(selfState.value == DuelState.BANG && peerState.value == DuelState.BANG) {
            //check who won
            Log.i(TAG, "CHECKING $selfBangDelta $peerBangDelta")
            //Option 1: to win the delay must be positive, but the smallest
            //Option 2: closest to target wins, even if it's early

            if(didSelfWin()) {
                duelServer.enqueueOutgoing(Message(Type.DAMAGE, damageToPeer.toString()))
                otherPeer.value = otherPeer.value.copy(health = otherPeer.value.health - damageToPeer)
            }
        }
    }

    //Option 1
    private fun didSelfWin() = selfBangDelta > 0 && if(peerBangDelta > 0) selfBangDelta < peerBangDelta else true

    private fun printStatus(){
        Log.i(TAG, "S:${selfState.value}\tP:${peerState.value}")
    }

}