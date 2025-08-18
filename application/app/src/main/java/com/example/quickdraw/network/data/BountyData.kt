package com.example.quickdraw.network.data

import kotlinx.serialization.Serializable

@Serializable
data class LeaderboardEntry(
    val id: Int,
    val bounty: Int,
    val username: String
)