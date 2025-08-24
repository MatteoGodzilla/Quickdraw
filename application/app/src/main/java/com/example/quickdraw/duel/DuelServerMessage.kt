package com.example.quickdraw.duel

import java.net.Socket

enum class Type {
    ACK,    //data contains type of message acknowledged
    SETUP,  //for checking that the other side is a Quickdraw peer, must be acknowledged
    READY,  //for notifying that a player has chosen a gun
    STEADY, //for starting the round
    BANG,   //for when a player shoots a bullet
    DAMAGE, //sent from winner to loser, to mark what gun has been used (damage is derived from that)
    NEW_ROUND,  //Sent to notify that a peer wants to start a new round, so state should reset,
    DONE    //Sent at the end of the entire duel
}

data class Message (val type: Type, val data: String = ""){
    companion object {
        fun deserialize(s: String) : Message{
            val tokens = s.split(':')
            val type = tokens.first().toInt()
            val value = tokens.subList(1, tokens.size).joinToString(":")
            return Message(Type.entries[type], value)
        }
    }
    fun serialize(): String{
        return "${type.ordinal}:${data}"
    }
}

interface MessageHandler{
    suspend fun onConnection(duelServer: DuelServer)
    suspend fun handleIncoming(message: Message, other: Socket)
}
