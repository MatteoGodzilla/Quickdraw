package com.example.quickdraw.network.data

import kotlinx.serialization.Serializable

@Serializable
data class LeaderboardEntry(
    val bounty: Int,
    val username: String
)