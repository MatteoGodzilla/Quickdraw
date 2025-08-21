package com.example.quickdraw.duel.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavHostController
import com.example.quickdraw.R
import com.example.quickdraw.duel.DuelGameLogic
import com.example.quickdraw.duel.DuelNavigation
import com.example.quickdraw.game.repo.GameRepository
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuelContainer(controller: NavHostController, gameLogic: DuelGameLogic, repo: GameRepository, content:@Composable ()->Unit){
    //pass to next state
    val selfState = gameLogic.selfState.collectAsState()
    val opponentState = gameLogic.peerState.collectAsState()
    //selfState.value== DuelState.CAN_PLAY && opponentState.value== DuelState.CAN_PLAY
        Scaffold(
            topBar = { DuelTopBar("Test opponent") },
            bottomBar = { DuelTopBar(repo.player.player.collectAsState().value.username) },
            modifier = Modifier.fillMaxSize()
        ){ padding->
            Box(modifier = Modifier.padding(padding)){
                content()
            }

        }
    }
