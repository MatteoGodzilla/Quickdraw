package com.example.quickdraw.game.vm

import androidx.lifecycle.ViewModel
import com.example.quickdraw.ImageLoader
import com.example.quickdraw.game.repo.GameRepository

class LeaderboardVM(
    repository: GameRepository,
    val imageLoader: ImageLoader
) : ViewModel() {
    val friends = repository.leaderboard.friends
    val globals = repository.leaderboard.global

    fun getPlayerImage(id:Int) = imageLoader.getPlayerFlow(id)
}