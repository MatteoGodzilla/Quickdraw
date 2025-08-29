package com.example.quickdraw.network.data

import kotlinx.serialization.Serializable

@Serializable
data class RoundSubmit(
    val won: Int,
    val idWeaponUsed: Int,
    val bulletsUsed: Int,
    val damage: Int
)

@Serializable
data class DuelSubmit(
    val authToken: String,
    val idOpponent: Int,
    val rounds: List<RoundSubmit>
)

@Serializable
data class RoundStatistics(
    val played:Int,
    val won: Int,
    val lost: Int,
    val bulletsShot: Int,
    val damageDealt: Int,
    val damageReceived: Int
)