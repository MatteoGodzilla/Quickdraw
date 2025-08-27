package com.example.quickdraw.duel.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import com.example.quickdraw.duel.DuelGameLogic
import com.example.quickdraw.duel.DuelNavigation
import com.example.quickdraw.duel.MatchResult
import com.example.quickdraw.duel.Message
import com.example.quickdraw.duel.duelBandit.DuelBanditLogic
import com.example.quickdraw.duel.Peer
import com.example.quickdraw.game.repo.GameRepository
import com.example.quickdraw.game.screen.StatsDisplayer
import com.example.quickdraw.ui.theme.Typography
import com.example.quickdraw.ui.theme.primaryButtonColors
import com.example.quickdraw.ui.theme.secondaryButtonColors

@Composable
fun ResultsScreen(controller: NavHostController, self: Peer, other: Peer, gameLogic: DuelGameLogic, repo: GameRepository){
    //this definitely needs a view manager, but
    val roundResults = gameLogic.duelState.collectAsState().value.roundResults
    val wonBySelf = roundResults.count { r -> r.didSelfWin == MatchResult.WON }
    val wonByOther = roundResults.count { r -> r.didSelfWin == MatchResult.LOST }
    val roundNumber = roundResults.size
    val weapon = repo.inventory.weapons.collectAsState().value.firstOrNull { w -> w.id == roundResults.last().weaponId }
    val bulletsRemaining = repo.inventory.bullets.collectAsState().value.firstOrNull { b -> b.type == weapon?.bulletType }
    val isOpponentFriendAlready = repo.leaderboard.friends.collectAsState().value.any { p -> p.id == self.id }
    DuelContainer(self, other) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center){
                Text( wonByOther.toString(), fontSize = Typography.titleLarge.fontSize * 3)
            }
            if(gameLogic.canGoToNextRound()){
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center){
                    Text( "Round $roundNumber")
                }
            }
            else{
                val message = if(wonByOther<wonBySelf) "VICTORY" else "DEFEAT"
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center){
                    Text(message)
                }
            }

            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center){
                Text( wonBySelf.toString(), fontSize = Typography.titleLarge.fontSize * 3)
            }
            if(gameLogic.canGoToNextRound()) {
                StatsDisplayer("Remaining bullets", bulletsRemaining?.amount.toString())
                Button(onClick = {
                        controller.navigate(DuelNavigation.WeaponSelect)
                        gameLogic.nextRound()
                    },
                    colors = secondaryButtonColors,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Change weapon")
                }
                Button(onClick = {
                        gameLogic.setReady(weapon!!)
                    },
                    colors = primaryButtonColors,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Next round", fontSize = Typography.titleLarge.fontSize)
                }
            } else {
                Row {
                    /*
                    Button(onClick = {},
                        colors = secondaryButtonColors,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Add memory")
                    }

                     */
                    Button(onClick = gameLogic::addFriend,
                        colors = secondaryButtonColors,
                        modifier = Modifier.weight(1f),
                        enabled = !isOpponentFriendAlready
                    ) {
                        Text("Send friend request")
                    }
                }
                Button(onClick = gameLogic::goodbye,
                    colors = primaryButtonColors,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("End duel", fontSize = Typography.titleLarge.fontSize)
                }
            }
        }
    }
}

@Composable
fun ResultsScreen(controller: NavHostController,  gameLogic: DuelBanditLogic, repo: GameRepository){
    //this definitely needs a view manager, but
    val roundResults = gameLogic.duelHistory.collectAsState().value
    val wonBySelf = roundResults.count { r -> r.wins }
    val wonByOther = roundResults.count { r -> !r.wins }
    val roundNumber = roundResults.size
    val weapon = repo.inventory.weapons.collectAsState().value.first { w -> w.id == roundResults.last().idWeapon }
    val bulletsRemaining = repo.inventory.bullets.collectAsState().value.first { b -> b.type == weapon.bulletType }
    DuelContainer(gameLogic, repo.player) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center){
                Text( wonByOther.toString(), fontSize = Typography.titleLarge.fontSize * 3)
            }
            if(!gameLogic.isDuelOver()){
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center){
                    Text( "Round $roundNumber")
                }
            }
            else{
                LaunchedEffect(true) {
                    gameLogic.sendToServer()
                }
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center){
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.fillMaxWidth().align(Alignment.CenterVertically)){
                            val rewards = repo.bandits.latestReward.collectAsState()
                            val message = gameLogic.getEndGameMessage()
                            if(message == "You defeated the bandit!"){
                                Text("VICTORY", textAlign = TextAlign.Center, fontSize = Typography.titleLarge.fontSize, modifier = Modifier.fillMaxWidth())
                                Text(message,modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                                if(rewards.value.money>0 || rewards.value.exp > 0){
                                    Text("You got ${rewards.value.money} coins and ${rewards.value.exp} exp ",modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                                }
                                else{
                                    Text("Obtaining rewards...",modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                                }
                            }
                            else{
                                Text("DEFEAT", textAlign = TextAlign.Center, fontSize = Typography.titleLarge.fontSize,modifier = Modifier.fillMaxWidth())
                                Text(message,modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                            }
                        }
                    }
                }
            }
            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center){
                Text( wonBySelf.toString(), fontSize = Typography.titleLarge.fontSize * 3)
            }
            if(!gameLogic.isDuelOver()) {
                StatsDisplayer("Remaining bullets", bulletsRemaining.amount.toString())
                Button(onClick = {
                    controller.navigate(DuelNavigation.WeaponSelect)
                    gameLogic.resetToSelect()
                },
                    colors = secondaryButtonColors,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Change weapon")
                }
                Button(onClick = {
                    gameLogic.resetToSelect()
                    gameLogic.setWeaponAndStart(weapon)
                    controller.navigate(DuelNavigation.Play)
                },
                    colors = primaryButtonColors,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Next round", fontSize = Typography.titleLarge.fontSize)
                }
            } else {
                Button(onClick = {gameLogic.sendBackToMainGame()},
                    colors = primaryButtonColors,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("End duel", fontSize = Typography.titleLarge.fontSize)
                }
            }
        }
    }
}