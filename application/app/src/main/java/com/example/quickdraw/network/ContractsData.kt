package com.example.quickdraw.network

import kotlinx.serialization.Serializable

@Serializable
data class ActiveContract(
    val activeId: Int,
    val name: String,
    val requiredTime: Long,
    val startTime: Long
)

@Serializable
data class AvailableContract(
    val id: Int,
    val name: String,
    val requiredTime: Long,
    val requiredPower: Int,
    val maxMercenaries: Int,
    val startCost: Int,
)

/* --- Request --- */
@Serializable
data class ContractStartRequest(
    val authToken: String,
    val contract: Int,
    val mercenaries: List<Int>
)

@Serializable
data class ContractRedeemRequest(
    val authToken: String,
    val idContract: Int
)

/* --- Response --- */
@Serializable
data class ActiveContractResponse(
    val contracts: List<ActiveContract>
)
@Serializable
data class AvailableContractResponse(
    val contracts: List<AvailableContract>
)

@Serializable
data class ContractRedeemResponse(
    val success: Boolean,
    val reward: Int
)
