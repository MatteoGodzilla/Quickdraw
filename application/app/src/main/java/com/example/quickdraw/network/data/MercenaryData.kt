package com.example.quickdraw.network.data

import kotlinx.serialization.Serializable


/* --- OBJECTS --- */
@Serializable
data class EmployedMercenary(
    val idEmployment: Int,
    val name: String,
    val power: Int
)

@Serializable
data class HireableMercenary(
    val id: Int,
    val name: String,
    val power: Int,
    val cost: Int
)

@Serializable
data class LockedMercenary(
    val id: Int,
    val name: String,
    val power: Int,
    val levelRequired: Int
)

/* --- REQUEST --- */

@Serializable
data class MercenaryHireable(val authToken: String)

@Serializable
data class MercenaryEmployed(val authToken: String)

@Serializable
data class EmployMercenary(val authToken: String,val idMercenary: Int)

@Serializable
data class MercenaryUnassigned(val authToken: String)


/* --- RESPONSE --- */
@Serializable
data class MercenaryHireableResponse(val mercenaries: List<HireableMercenary>)

@Serializable
data class EmployMercenaryResponse(val idEmployment: Int)

@Serializable
data class MercenaryEmployedResponse(val mercenaries: List<EmployedMercenary>)

@Serializable
data class UnassignedMercenaryResponse(val mercenaries: List<EmployedMercenary>)

@Serializable
data class NextToUnlockMercenaryResponse(val mercenaries: List<LockedMercenary>)