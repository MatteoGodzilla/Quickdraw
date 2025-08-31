package com.example.quickdraw.duel

import kotlinx.serialization.Serializable

@Serializable
data class Peer(
    val id: Int,
    val username: String,
    val level: Int,
    val health: Int,
    val maxHealth: Int,
    val bounty: Int
) {
    fun getValuesAsMap(): Map<String, String> {
        return mapOf(
            ID_KEY to id.toString(),
            USERNAME_KEY to username,
            LEVEL_KEY to level.toString(),
            HEALTH_KEY to health.toString(),
            MAX_HEALTH_KEY to maxHealth.toString(),
            BOUNTY_KEY to bounty.toString()
        )
    }

    companion object {
        const val ID_KEY = "id"
        const val USERNAME_KEY = "username"
        const val LEVEL_KEY = "level"
        const val HEALTH_KEY = "health"
        const val MAX_HEALTH_KEY = "maxHealth"
        const val BOUNTY_KEY = "bounty"
    }
}
