package com.example.quickdraw.duel

import java.net.Socket

enum class Type {
    ACK, //data contains type of message acknowledged
    HELLO, //no data
    READY,
    STEADY,
    BANG
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
    suspend fun onConnection(isServer: Boolean, duelServer: DuelServer)
    suspend fun handleC2SMessage(message: Message, client: Socket)
    suspend fun handleS2CMessage(message: Message, server: Socket)
}
