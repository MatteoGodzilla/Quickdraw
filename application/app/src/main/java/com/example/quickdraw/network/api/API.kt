package com.example.quickdraw.network.api

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

const val LOGIN_ENDPOINT = "/auth/login"
const val TOKEN_LOGIN_ENDPOINT = "/auth/tokenLogin"
const val REGISTER_ENDPOINT = "/auth/register"

const val INVENTORY_ENDPOINT = "/inventory"
const val STATUS_ENDPOINT = "/status"
const val LEVELS_ENDPOINT = "/status/levels"
const val BASE_STATS_ENDPOINT = "/status/baseStats"

const val CONTRACTS_ACTIVE_ENDPOINT = "/contracts/active"
const val CONTRACTS_AVAILABLE_ENDPOINT = "/contracts/available"
const val CONTRACTS_START = "/contracts/start"
const val CONTRACTS_REDEEM = "/contracts/redeem"

const val SHOP_WEAPONS = "/shop/weapons"
const val SHOP_BULLETS = "/shop/bullets"
const val SHOP_MEDIKITS = "/shop/medikits"
const val SHOP_UPGRADES = "/shop/upgrades"
const val SHOP_BUY_BULLETS = "$SHOP_BULLETS/buy"
const val SHOP_BUY_MEDIKIT = "$SHOP_MEDIKITS/buy"
const val SHOP_BUY_WEAPON = "$SHOP_WEAPONS/buy"
const val SHOP_BUY_UPGRADE = "$SHOP_UPGRADES/buy"

const val BOUNTY_FRIENDS = "/bounty/friends"
const val BOUNTY_LEADERBOARD = "/bounty/leaderboard"

const val MERCENARY_HIREABLE = "/mercenaries/hireable"
const val MERCENARY_EMPLOY = "/mercenaries/employ"
const val MERCENARY_PLAYER_ALL = "/mercenaries/player/all"
const val MERCENARY_PLAYER_UNASSIGNED = "/mercenaries/player/unassigned"
const val MERCENARY_NEXT_UNLOCKABLE = "/mercenaries/nextUnlockables"

const val IMAGE_WEAPON = "/image/weapon"
const val IMAGE_BULLET = "/image/bullet"
const val IMAGE_MEDIKIT = "/image/medikit"
const val IMAGE_UPGRADE = "/image/upgrade"
const val IMAGE_PLAYER = "/image/player"
const val IMAGE_UPDATE_PLAYER_PIC = "/image/updatePic"

const val USE_MEDIKIT = "/use/medikit"

const val BANDITS_GET = "/bandit/pool"

const val DUEL_ENDPOINT = "/duel"

inline fun <reified T> T.toRequestBody(): RequestBody {
    val jsonString = Json.encodeToString(this)
    return jsonString.toRequestBody("application/json".toMediaType())
}

