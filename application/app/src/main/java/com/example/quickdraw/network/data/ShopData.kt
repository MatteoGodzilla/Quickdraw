package com.example.quickdraw.network.data

import kotlinx.serialization.Serializable

@Serializable
data class ShopWeapon(
    val id: Int,
    val name: String,
    val damage: Int,
    val cost: Int
)

@Serializable
data class ShopBullet(
    val id: Int,
    val type: Int,
    val name: String,
    val cost: Int,
    val quantity: Int,
    val capacity: Int
)

@Serializable
data class ShopMedikit(
    val id: Int,
    val idMedikit: Int,
    val description: String,
    val healthRecover: Int,
    val cost: Int,
    val quantity: Int,
    val capacity: Int
)


@Serializable
data class ShopUpgrade(
    val id: Int,
    val type: Int,
    val description: String,
    val level: Int,
    val cost: Int
)

//request
@Serializable
data class BuyRequest(
    val authToken:String,
    val id:Int
)

