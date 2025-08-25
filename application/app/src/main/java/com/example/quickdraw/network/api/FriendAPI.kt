package com.example.quickdraw.network.api

import com.example.quickdraw.network.ConnectionManager
import com.example.quickdraw.network.data.FriendRequest

fun addFriendAPI(authToken: String, friendId: Int): Boolean {
    val body = FriendRequest(authToken, friendId).toRequestBody()
    val response = ConnectionManager.attempt(body, BOUNTY_FRIENDS_ADD)
    return response?.code == 200
}

fun removeFriendAPI(authToken: String, friendId: Int): Boolean {
    val body = FriendRequest(authToken, friendId).toRequestBody()
    val response = ConnectionManager.attempt(body, BOUNTY_FRIENDS_REMOVE)
    return response?.code == 200
}
