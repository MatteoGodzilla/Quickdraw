package com.example.quickdraw.network.data

import com.example.quickdraw.game.repo.Evaluation
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
data class BaseStats(
    val upgradeType:Int,
    val baseValue:Int,
    val evaluation: Evaluation
)
