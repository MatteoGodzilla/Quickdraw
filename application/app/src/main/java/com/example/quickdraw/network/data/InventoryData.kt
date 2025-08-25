package com.example.quickdraw.network.data

import kotlinx.serialization.Serializable

@Serializable
data class InventoryBullet(
    val type: Int,
    val description: String,
    val amount: Int,
    val capacity: Int
)
@Serializable
data class InventoryWeapon(
    val id: Int,
    val name: String,
    val damage: Int,
    val cost: Int,
    val bulletType: Int,
    val bulletsShot: Int
)
@Serializable
data class InventoryMedikit(
    val healthRecover: Int,
    val description: String,
    val amount: Int,
    val capacity: Int,
    val id:Int
)
@Serializable
data class InventoryUpgrade(
    val idUpgrade: Int,
    val description: String,
    val type: Int,
    val level:Int,
    val modifier:Int
)
@Serializable
data class InventoryResponse(
    val bullets: List<InventoryBullet>,
    val weapons: List<InventoryWeapon>,
    val medikits: List<InventoryMedikit>,
    val upgrades: List<InventoryUpgrade>
)

