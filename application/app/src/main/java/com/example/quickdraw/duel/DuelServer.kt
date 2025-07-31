package com.example.quickdraw.duel

import java.net.InetAddress

//Actual game server that handles the comms for rounds and the general duel
class DuelServer {
    fun startAsServer(){
        //listen on PORT
    }

    fun startAsClient(address: InetAddress){
        //send request on address:PORT
    }

    companion object{
        const val PORT = 54321
    }
}