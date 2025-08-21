package com.example.quickdraw.game.screen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import com.example.quickdraw.game.components.BasicScreen
import com.example.quickdraw.game.components.BountyEntry
import com.example.quickdraw.game.components.ContentTab
import com.example.quickdraw.game.vm.LeaderboardVM
import kotlinx.coroutines.runBlocking

@Composable
fun LeaderBoardScreen ( viewModel: LeaderboardVM, controller: NavHostController ) {

    //collected states
    val friends = viewModel.friends.collectAsState()
    val globals = viewModel.globals.collectAsState()

    BasicScreen("Bounty Board", controller, listOf(
        ContentTab("Friends"){
            if(friends.value.isNotEmpty()){
                var counter = 1
                for(entry in friends.value) {
                    BountyEntry(entry,counter, viewModel.getPlayerImage(entry.id).collectAsState().value )
                    counter++
                }
            } else {
                Text("You haven't added any friends yet!\nDuel them first, then add them in the result screen",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        },
        ContentTab("Leaderboard"){
            if(globals.value.isNotEmpty()){
                var counter = 1
                for(entry in globals.value) {
                    BountyEntry(entry,counter, viewModel.getPlayerImage(entry.id).collectAsState().value)
                    counter++
                }
            }
        }
    ))
}