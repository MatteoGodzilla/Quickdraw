package com.example.quickdraw.game.repo

import com.example.quickdraw.duel.Peer
import kotlinx.coroutines.flow.MutableStateFlow

//Local only, used as a bridge between PeerFinder and ui
class PeerRepository {
    val scanning: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val peers: MutableStateFlow<List<Peer>> = MutableStateFlow(listOf(
        Peer("Testing", 1),
        Peer("Testing 2", 1),
    ))
}