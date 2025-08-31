package com.example.quickdraw.duel.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.quickdraw.duel.Peer
import com.example.quickdraw.duel.duelBandit.DuelBanditLogic
import com.example.quickdraw.game.repo.PlayerRepository
import com.example.quickdraw.ui.theme.QuickdrawTheme

@Composable
fun DuelContainer(self: Peer, opponent: Peer, content:@Composable ()->Unit){
    QuickdrawTheme {
        Scaffold(
            topBar = { DuelBar(opponent.username, opponent.health.toFloat() / opponent.maxHealth, Color.Red) },
            bottomBar = { DuelBar(self.username,self.health.toFloat() / self.maxHealth, Color.Blue) },
            modifier = Modifier.padding(
                top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            )
        ){ padding->
            Box(modifier = Modifier.padding(padding)){
                content()
            }
        }
    }
}