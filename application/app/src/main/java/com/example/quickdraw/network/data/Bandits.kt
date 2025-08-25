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

@Serializable
data class FightAttempt(
    val wins: Boolean,
    val idWeapon:Int,
    val banditDamage: Int
)

@Serializable
data class FightBanditRequest(
    val authToken:String,
    val idIstance:Int,
    val fights:List<FightAttempt>
)

@Serializable
data class Rewards(
    val money:Int,
    val exp:Int
)