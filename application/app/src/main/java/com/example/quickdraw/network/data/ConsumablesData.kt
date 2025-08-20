package com.example.quickdraw.network.data

import kotlinx.serialization.Serializable

@Serializable
data class UseMedikitRequest(
    val authToken: String,
    val type: Int
)

@Serializable
data class UseMedikitResponse(
    val newHealth: Int,
    val amountLeft: Int
)