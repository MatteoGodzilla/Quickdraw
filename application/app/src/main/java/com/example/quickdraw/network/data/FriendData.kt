package com.example.quickdraw.network.data

import kotlinx.serialization.Serializable

@Serializable
data class FriendRequest(
    val authToken: String,
    val idFriend: Int
)
