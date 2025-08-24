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
import com.example.quickdraw.duel.Peer
import com.example.quickdraw.game.repo.GameRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuelContainer(self: Peer, opponent: Peer, content:@Composable ()->Unit){
    Scaffold(
        topBar = { DuelBar(opponent.username, opponent.health.toFloat() / opponent.maxHealth, Color.Red) },
        bottomBar = { DuelBar(self.username,self.health.toFloat() / self.maxHealth, Color.Blue) },
        modifier = Modifier.fillMaxSize()
    ){ padding->
        Box(modifier = Modifier.padding(padding)){
            content()
        }
    }
}
