package com.example.quickdraw.network.data

import kotlinx.serialization.Serializable

@Serializable
data class ImageRequest(val id:Int)

@Serializable
data class ImageResponse(val image:String)