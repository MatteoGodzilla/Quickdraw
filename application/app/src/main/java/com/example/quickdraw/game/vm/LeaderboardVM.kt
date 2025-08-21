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

    fun getPlayerImage(id:Int) = imageLoader.getPlayerFlow(id)
}