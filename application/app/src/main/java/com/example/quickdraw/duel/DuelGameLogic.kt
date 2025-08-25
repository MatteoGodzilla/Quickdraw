package com.example.quickdraw.duel

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import com.example.quickdraw.TAG
import com.example.quickdraw.game.repo.GameRepository
import com.example.quickdraw.network.data.InventoryWeapon
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.net.Socket
import kotlin.random.Random

//Higher level logic for actually handling the game part
class DuelGameLogic(
    peer: Peer,
    private val repository: GameRepository,
    private val context: Context,
) : MessageHandler{
    val selfState = MutableStateFlow(PeerState.UNKNOWN)
    val otherState = MutableStateFlow(PeerState.UNKNOWN)
    val selfPeer = MutableStateFlow(peer)
    val otherPeer = MutableStateFlow(Peer(0, "", 1, 100, 100))

    val duelState = MutableStateFlow(DuelState())

    val shouldShoot = MutableStateFlow(false)

    //TEMPORARY VARIABLES
    //deciding when players should shoot
    private var selfChosenDelay = 0.0
    private var peerChosenDelay = 0.0
    var agreedBangDelay = 0.0
        private set
    //Time delta when players actually shot
    private var selfBangDelta = 0.0
    private var peerBangDelta = 0.0
    //Reference
    var referenceTimeMS = 0L
        private set
    //Weapon
    private lateinit var chosenWeapon: InventoryWeapon

    private lateinit var duelServer: DuelServer
    private val localScope = CoroutineScope(Dispatchers.IO)

    override suspend fun onConnection(duelServer: DuelServer) {
        this.duelServer = duelServer
        duelServer.enqueueOutgoing(Message(MessageType.SETUP, Json.encodeToString(selfPeer.value)))
    }

    override suspend fun handleIncoming(message: Message, other: Socket) {
        when(message.type){
            MessageType.ACK -> {
                if(selfState.value == PeerState.UNKNOWN){
                    if(message.data == MessageType.SETUP.toString()){
                        selfState.value = PeerState.CAN_PLAY
                        printStatus()
                    } //otherwise it should throw something or error
                }
            }
            MessageType.SETUP -> {
                if(otherState.value == PeerState.UNKNOWN){
                    otherState.value = PeerState.CAN_PLAY
                    otherPeer.value = Json.decodeFromString(message.data)
                    printStatus()
                    duelServer.enqueueOutgoing(Message(MessageType.ACK, message.type.toString()))
                }
            }
            MessageType.READY -> {
                if(otherState.value == PeerState.CAN_PLAY || otherState.value == PeerState.BANG) {
                    otherState.value = PeerState.READY
                    printStatus()
                    checkReady()
                }
            }
            MessageType.STEADY -> {
                if(otherState.value == PeerState.READY) {
                    peerChosenDelay = message.data.toDouble()
                    otherState.value = PeerState.STEADY
                    printStatus()
                    checkSteady()
                }
            }
            MessageType.BANG -> {
                if(otherState.value == PeerState.STEADY){
                    //Get time from peer and check
                    otherState.value = PeerState.BANG
                    printStatus()
                    peerBangDelta = message.data.toDouble()
                    checkBang()
                }
            }
            MessageType.DAMAGE -> {
                if(selfState.value == PeerState.BANG){
                    //apply damage from opponent if self has lost the round
                    if(didSelfWin() == MatchResult.LOST) {
                        val damageReceived = message.data.toInt()
                        Log.i(TAG, "Accepting damage: $damageReceived")
                        selfPeer.value = selfPeer.value.copy(health = selfPeer.value.health - damageReceived)
                        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                        vibrator.vibrate(VibrationEffect.createOneShot(1500, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        Log.i(TAG, "There is DEFINETLY SOMETHING WRONG")
                    }
                }
            }
            MessageType.NEW_ROUND -> {
                otherState.value = PeerState.CAN_PLAY
                printStatus()
            }
            MessageType.DONE -> {
                otherState.value = PeerState.DONE
                printStatus()
            }
        }
    }

    //UI Functions

    fun setReady(weapon: InventoryWeapon) = localScope.launch{
        selfState.value = PeerState.READY
        printStatus()
        shouldShoot.value = false
        chosenWeapon = weapon
        duelServer.enqueueOutgoing(Message(MessageType.READY))
        checkReady()
    }

     fun bang() = localScope.launch{
        if(selfState.value == PeerState.STEADY){
            selfState.value = PeerState.BANG
            printStatus()
            selfBangDelta = System.currentTimeMillis() - referenceTimeMS - agreedBangDelay
            duelServer.enqueueOutgoing(Message(MessageType.BANG, selfBangDelta.toString()))
            repository.inventory.bullets.value = repository.inventory.bullets.value.map { b ->
                if(b.type == chosenWeapon.bulletType) b.copy(amount = b.amount - 1)
                else b
            }
            checkBang()
        }
    }

    fun nextRound() = localScope.launch{
        if(canGoToNextRound()){
            selfState.value = PeerState.CAN_PLAY
            printStatus()
            duelServer.enqueueOutgoing(Message(MessageType.NEW_ROUND))
            resetTempVariables()
        } else {
            selfState.value = PeerState.DONE
            printStatus()
            duelServer.enqueueOutgoing(Message(MessageType.DONE))
        }
    }

    private fun startPolling() = localScope.launch{
        delay(MIN_DELAY.toLong() / 2)
        while(selfState.value == PeerState.STEADY)  {
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
    fun canGoToNextRound(): Boolean {
        return duelState.value.roundResults.count { r -> r.didSelfWin != MatchResult.DRAW } < duelState.value.roundsToWin
    }

    //Stuff to do when both peers are in the same state
    private suspend fun checkReady(){
        if(selfState.value == PeerState.READY && otherState.value == PeerState.READY) {
            selfState.value = PeerState.STEADY
            printStatus()
            selfChosenDelay = Random.nextDouble(MIN_DELAY,MAX_DELAY) //milliseconds
            duelServer.enqueueOutgoing(Message(MessageType.STEADY, selfChosenDelay.toString()))
            checkSteady()
        }
    }

    private fun checkSteady(){
        if(selfState.value == PeerState.STEADY && otherState.value == PeerState.STEADY){
            //set reference time
            referenceTimeMS = System.currentTimeMillis()
            agreedBangDelay = (selfChosenDelay + peerChosenDelay) / 2
            Log.i(TAG, "[] Agreed on delay $agreedBangDelay")
            startPolling()
        }
    }

    private suspend fun checkBang() {
        if(selfState.value == PeerState.BANG && otherState.value == PeerState.BANG) {
            //check who won
            Log.i(TAG, "CHECKING $selfBangDelta $peerBangDelta")
            duelState.value = duelState.value.copy(roundResults = duelState.value.roundResults +
                RoundData(didSelfWin(), chosenWeapon.id, chosenWeapon.damage, referenceTimeMS, agreedBangDelay, selfBangDelta, peerBangDelta)
            )

            //send to server for applying damage/use of bullets

            if(didSelfWin() == MatchResult.WON) {
                duelServer.enqueueOutgoing(Message(MessageType.DAMAGE, chosenWeapon.damage.toString()))
                otherPeer.value = otherPeer.value.copy(health = otherPeer.value.health - chosenWeapon.damage)
            }
        }
    }

    private fun resetTempVariables() {
        selfChosenDelay = 0.0
        peerChosenDelay = 0.0
        agreedBangDelay = 0.0
        selfBangDelta = 0.0
        peerBangDelta = 0.0
        referenceTimeMS = 0
    }

    //Option 1: to win the delay must be positive, but the smallest
    //Option 2: closest to target wins, even if it's early
    //Option 1
    fun didSelfWin(): MatchResult {
        return if(selfBangDelta < 0){
            if(peerBangDelta < 0){
                MatchResult.DRAW
            } else {
                MatchResult.LOST
            }
        } else {
            if(peerBangDelta < 0 || selfBangDelta < peerBangDelta){
                MatchResult.WON
            } else {
                MatchResult.LOST
            }
        }
    }

    private fun printStatus(){
        Log.i(TAG, "S:${selfState.value}\tP:${otherState.value}")
    }

}