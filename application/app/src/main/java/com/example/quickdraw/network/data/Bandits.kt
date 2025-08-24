package com.example.quickdraw.network.data

import kotlinx.serialization.Serializable

@Serializable
data class Bandit(
    val name: String,
    val hp:Int,
    val minDamage:Int,
    val maxDamage:Int,
    val minSpeed:Int,
    val maxSpeed:Int
)

@Serializable
data class GetBanditsResponse(
    val expires: String, //must be converted after
    val idIstance:Int,
    val stats:Bandit
)