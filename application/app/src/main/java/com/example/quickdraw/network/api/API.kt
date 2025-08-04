package com.example.quickdraw.network.api

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

private const val BASE_URL = "http://192.168.1.63:8000"

const val LOGIN_ENDPOINT = "$BASE_URL/auth/login"
const val TOKEN_LOGIN_ENDPOINT = "$BASE_URL/auth/tokenLogin"
const val REGISTER_ENDPOINT = "$BASE_URL/auth/register"

const val INVENTORY_ENDPOINT = "$BASE_URL/inventory"
const val STATUS_ENDPOINT = "$BASE_URL/status"
const val LEVELS_ENDPOINT = "$BASE_URL/status/levels"

const val CONTRACTS_ACTIVE_ENDPOINT = "$BASE_URL/contracts/active"
const val CONTRACTS_AVAILABLE_ENDPOINT = "$BASE_URL/contracts/available"
const val CONTRACTS_START = "$BASE_URL/contracts/start"
const val CONTRACTS_REDEEM = "$BASE_URL/contracts/redeem"

const val SHOP_WEAPONS = "$BASE_URL/shop/weapons"
const val SHOP_BULLETS = "$BASE_URL/shop/bullets"
const val SHOP_MEDIKITS = "$BASE_URL/shop/medikits"
const val SHOP_UPGRADES = "$BASE_URL/shop/upgrades"

const val BOUNTY_FRIENDS = "$BASE_URL/bounty/friends"
const val BOUNTY_LEADERBOARD = "$BASE_URL/bounty/leaderboard"

const val MERCENARY_HIREABLE = "$BASE_URL/hireable"

const val MERCENARY_EMPLOY = "$BASE_URL/employ"

const val MERCENARY_PLAYER_ALL = "$BASE_URL/player/all"

const val MERCENARY_PLAYER_UNASSIGNED = "$BASE_URL/player/unassigned"

inline fun <reified T> T.toRequestBody(): RequestBody {
    val jsonString = Json.encodeToString(this)
    return jsonString.toRequestBody("application/json".toMediaType())
}

