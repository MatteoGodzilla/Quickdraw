package com.example.quickdraw.network

import kotlinx.serialization.Serializable

/* --- RESPONSE --- */
@Serializable
data class PlayerStatus(
    val id: Int,
    val health: Int,
    val maxHealth: Int,
    val exp: Int,
    val money: Int,
    val bounty: Int,
    val username: String
)
