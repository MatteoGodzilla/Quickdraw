package com.example.quickdraw.game.repo

import kotlinx.serialization.Serializable

@Serializable
data class Contract(
    val id: Int,
    val name: String,
    val requiredTime: Long,
    val requiredPower: Int,
    val maxMercenaries: Int,
    val startCost: Int,
)

@Serializable
data class ContractResponse(
    val contracts: List<Contract>
)