package com.example.quickdraw.duel.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.example.quickdraw.duel.DuelGameLogic
import com.example.quickdraw.game.repo.GameRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuelContainer(controller: NavHostController, gameLogic: DuelGameLogic, repo: GameRepository, content:@Composable ()->Unit){
    //pass to next state
    val selfState = gameLogic.selfState.collectAsState()
    val opponentState = gameLogic.peerState.collectAsState()
    //selfState.value== DuelState.CAN_PLAY && opponentState.value== DuelState.CAN_PLAY
        Scaffold(
            topBar = { DuelBar("Test opponent",Color.Red) },
            bottomBar = { DuelBar(repo.player.player.collectAsState().value.username,Color.Blue) },
            modifier = Modifier.fillMaxSize()
        ){ padding->
            Box(modifier = Modifier.padding(padding)){
                content()
            }

        }
    }
