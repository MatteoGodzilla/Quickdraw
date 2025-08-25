package com.example.quickdraw.duel

enum class MatchResult {
    WON,
    LOST,
    DRAW
}

data class RoundData (
    val didSelfWin: MatchResult,
    val weaponId: Int,
    val damage: Int, //depending on the boolean, it changes from inflicted to received
    val selfReferenceTime: Long,
    val agreedDelta: Double,
    val selfDelta: Double,
    val otherDelta: Double
)

//current round is derived from the list, based on the round result
//damage received is also derived from list
//overall match result is also also derived from the list
data class DuelState(
    val roundsToWin: Int = 3, //Best of 5, without counting draws
    val roundResults: List<RoundData> = listOf()
)

const val MIN_DELAY = 5000.0 //milliseconds aka 5 seconds
const val MAX_DELAY = 10000.0 //milliseconds aka 10 seconds

enum class PeerState {
    UNKNOWN,
    CAN_PLAY,
    READY,
    STEADY,
    BANG,
    DONE
}
