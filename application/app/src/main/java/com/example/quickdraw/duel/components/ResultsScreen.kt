package com.example.quickdraw.duel.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.quickdraw.duel.DuelGameLogic
import com.example.quickdraw.duel.DuelNavigation
import com.example.quickdraw.duel.MatchResult
import com.example.quickdraw.duel.duelBandit.DuelBanditLogic
import com.example.quickdraw.duel.Peer
import com.example.quickdraw.duel.PeerState
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
    val weapon = repo.inventory.weapons.collectAsState().value.first { w -> w.id == roundResults.last().weaponId }
    val bulletsRemaining = repo.inventory.bullets.collectAsState().value.first { b -> b.type == weapon.bulletType }
    val isOpponentFriendAlready = repo.leaderboard.friends.collectAsState().value.any { p -> p.id == self.id }
    val canGoToNextRound =  gameLogic.canGoToNextRound()
    val canReuseWeapon = bulletsRemaining.amount >= weapon.bulletsShot
    val selfIsReady =  gameLogic.selfState.collectAsState().value == PeerState.READY

    if(canGoToNextRound) {
        ResultScreenOngoing(self, other, roundNumber, wonBySelf, wonByOther, selfIsReady) {
            StatsDisplayer("Remaining bullets", bulletsRemaining.amount.toString())
            Button(
                onClick = {
                    controller.navigate(DuelNavigation.WeaponSelect)
                    gameLogic.nextRound()
                },
                colors = secondaryButtonColors,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Change weapon")
            }
            if (canReuseWeapon) {
                Button(
                    onClick = {
                        gameLogic.setReady(weapon)
                    },
                    colors = primaryButtonColors,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Next round", fontSize = Typography.titleLarge.fontSize)
                }
            }
        }
    } else {
        //TODO: add duel result for pvp
        val mainResult = if(wonByOther < wonBySelf) "VICTORY" else "DEFEAT"
        ResultScreenDone(self, other, wonBySelf, wonByOther, mainResult, "") {
            Row {
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

@Composable
fun ResultsScreen(controller: NavHostController, self:Peer, other:Peer, gameLogic: DuelBanditLogic, repo: GameRepository,){
    //this definitely needs a view manager, but
    val roundResults = gameLogic.duelHistory.collectAsState().value
    val wonBySelf = roundResults.count { r -> r.wins }
    val wonByOther = roundResults.count { r -> !r.wins }
    val roundNumber = roundResults.size
    val weapon = repo.inventory.weapons.collectAsState().value.first { w -> w.id == roundResults.last().idWeapon }
    val bulletsRemaining = repo.inventory.bullets.collectAsState().value.first { b -> b.type == weapon.bulletType }
    val canReuseWeapon = bulletsRemaining.amount >= weapon.bulletsShot

    if(!gameLogic.isDuelOver()){
        ResultScreenOngoing(self, other,roundNumber, wonBySelf, wonByOther, false) {
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
            if(canReuseWeapon){
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
            }
        }
    } else {
        LaunchedEffect(true) {
            gameLogic.sendToServer()
        }

        val extraText = remember{ mutableStateOf("Obtaining rewards...") }
        val rewards = repo.bandits.latestReward.collectAsState()
        if(rewards.value.money>0 || rewards.value.exp > 0){
            extraText.value = "You got ${rewards.value.money} coins and ${rewards.value.exp} exp "
        }
        if(gameLogic.forfeit.collectAsState().value){
            extraText.value = "You surrendered"
        }

        ResultScreenDone(self, other, wonBySelf, wonByOther, gameLogic.getEndGameMessage(), extraText.value) {
           Button(onClick = {gameLogic.sendBackToMainGame()},
               colors = primaryButtonColors,
               modifier = Modifier.fillMaxWidth()
           ) {
               Text("End duel", fontSize = Typography.titleLarge.fontSize)
           }
        }
    }
}

@Composable
fun ResultScreenOngoing(self: Peer, other: Peer, roundNumber: Int, wonBySelf: Int, wonByOther: Int, waitingForOpponent: Boolean, bottomContent: @Composable () -> Unit){
    DuelContainer(self, other) {
        if(waitingForOpponent){
            Box(modifier=Modifier.padding(5.dp).fillMaxSize()){
                Column(modifier = Modifier.fillMaxWidth().align(alignment = Alignment.Center)) {
                    LoadMessage("Waiting for opponent...",Modifier.align(alignment = Alignment.CenterHorizontally))
                }
            }
        }
        else{
            Column {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center){
                    Text( wonByOther.toString(), fontSize = Typography.titleLarge.fontSize * 3)
                }
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center){
                    Text( "Round $roundNumber")
                }
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center){
                    Text( wonBySelf.toString(), fontSize = Typography.titleLarge.fontSize * 3)
                }
                bottomContent()
            }
        }
    }
}

@Composable
fun ResultScreenDone(self: Peer, other: Peer, wonBySelf: Int, wonByOther: Int, mainResult: String, middleExtraText: String, bottomContent: @Composable ()->Unit){
    DuelContainer(self, other) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center){
                Text( wonByOther.toString(), fontSize = Typography.titleLarge.fontSize * 3)
            }
            Column(modifier = Modifier.fillMaxWidth().weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){
                Text(mainResult, fontSize = Typography.titleLarge.fontSize)
                Text(middleExtraText)
            }
            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center){
                Text( wonBySelf.toString(), fontSize = Typography.titleLarge.fontSize * 3)
            }
            bottomContent()
        }
    }
}