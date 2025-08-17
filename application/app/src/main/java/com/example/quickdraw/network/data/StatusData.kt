package com.example.quickdraw.network.data

import kotlinx.serialization.Serializable

/* --- RESPONSE --- */
@Serializable
data class PlayerInfo(
    val id: Int,
    val health: Int,
    val exp: Int,
    val money: Int,
    val bounty: Int,
    val username: String
)

@Serializable
data class PlayerStats(
    val idPlayer: Int,
    val maxHealth: Int,
    val expBoost: Int,
    val bountyBoost: Int,
    val moneyBoost: Int,
    val maxContracts: Int
)

@Serializable
data class PlayerStatus(
    val player: PlayerInfo,
    val stats: PlayerStats
)
