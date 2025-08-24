package com.example.quickdraw.duel.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.quickdraw.duel.DuelGameLogic
import com.example.quickdraw.duel.DuelNavigation
import com.example.quickdraw.duel.Peer

@Composable
fun ResultsScreen(controller: NavHostController, self: Peer, other: Peer, gameLogic: DuelGameLogic){
    DuelContainer(self, other) {
        if(gameLogic.canGoToNextRound()) {
            Button(onClick = {
                controller.navigate(DuelNavigation.WeaponSelect)
                gameLogic.nextRound()
            }) {
                Text("Next round")
            }
        } else {
            Text("GG")
        }
    }
}