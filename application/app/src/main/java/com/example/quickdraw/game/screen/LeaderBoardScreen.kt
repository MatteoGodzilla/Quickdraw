package com.example.quickdraw.game.screen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import com.example.quickdraw.ImageLoader
import com.example.quickdraw.game.components.BasicScreen
import com.example.quickdraw.game.components.ContentTab
import com.example.quickdraw.game.dataDisplayers.BountyEntry
import com.example.quickdraw.game.repo.GameRepository
import com.example.quickdraw.network.data.LeaderboardEntry
import kotlinx.coroutines.runBlocking

@Composable
fun LeaderBoardScreen (controller: NavHostController, repository: GameRepository, imageLoader: ImageLoader) {

    //collected states
    val friends = repository.leaderboard.friends.collectAsState()
    val globals = repository.leaderboard.global.collectAsState()

    BasicScreen("Bounty Board", controller, listOf(
        ContentTab("Friends"){
            if(friends.value.isNotEmpty()){
                var counter = 1
                for(entry in friends.value) {
                    BountyEntry(entry,counter, runBlocking { imageLoader.getPlayerImage(entry.id) } )
                    counter++
                }
            }
        },
        ContentTab("Leaderboard"){
            if(globals.value.isNotEmpty()){
                var counter = 1
                for(entry in globals.value) {
                    BountyEntry(entry,counter, runBlocking{ imageLoader.getPlayerImage(entry.id) } )
                    counter++
                }
            }
        }
    ))
}