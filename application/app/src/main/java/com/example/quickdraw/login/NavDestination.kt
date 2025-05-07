package com.example.quickdraw.login

import kotlinx.serialization.Serializable

class NavDestination{
    @Serializable
    object Login
    @Serializable
    data class Register(val initialEmail: String, val initialPassword: String)
}