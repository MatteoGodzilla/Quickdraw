package com.example.quickdraw.game.vm

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickdraw.ImageLoader
import com.example.quickdraw.game.repo.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LeaderboardVM(
    repository: GameRepository,
    private val imageLoader: ImageLoader
) : ViewModel() {
    val friends = repository.leaderboard.friends
    val globals = repository.leaderboard.global
    private val map: MutableMap<Int, MutableStateFlow<ImageBitmap>> = mutableMapOf()

    fun getPlayerImageFlow(id: Int): MutableStateFlow<ImageBitmap>{
        if(!map.containsKey(id)){
            map[id] = MutableStateFlow(imageLoader.imageNotFound.asImageBitmap())
        }
        viewModelScope.launch {
            map[id]!!.value = imageLoader.getPlayerImage(id)
        }
        return map[id]!!
    }
}