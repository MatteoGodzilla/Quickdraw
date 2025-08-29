package com.example.quickdraw.duel.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.quickdraw.R
import com.example.quickdraw.duel.DuelNavigation
import com.example.quickdraw.duel.Peer
import com.example.quickdraw.duel.duelBandit.DuelBanditLogic
import com.example.quickdraw.game.components.infiniteRotation
import com.example.quickdraw.game.repo.PlayerRepository
import kotlinx.coroutines.delay

@Composable
fun PresentationScreen(controller: NavHostController, self: Peer, other: Peer){
    LaunchedEffect(true) {
        delay(3000)
        controller.navigate(DuelNavigation.WeaponSelect)
    }
    DuelContainer(self, other) {
        Box(modifier=Modifier.padding(5.dp).fillMaxSize()){
            Column(modifier = Modifier.fillMaxWidth().align(alignment = Alignment.Center)) {
                LoadMessage("Starting match...",Modifier.align(alignment = Alignment.CenterHorizontally))
            }
        }
    }
}

//for bandits
@Composable
fun PresentationScreen(controller: NavHostController, vm: DuelBanditLogic, playerRepo:PlayerRepository){
    LaunchedEffect(true) {
        delay(3000)
        controller.navigate(DuelNavigation.WeaponSelect)
    }
    DuelContainer(vm, playerRepo) {
        Box(modifier=Modifier.padding(5.dp).fillMaxSize()){
            Column(modifier = Modifier.fillMaxWidth().align(alignment = Alignment.Center)) {
                LoadMessage("Starting match...",Modifier.align(alignment = Alignment.CenterHorizontally))
            }
        }
    }
}

@Composable
fun LoadMessage(message:String, modifier:Modifier){
        Text(message, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.radar_24px),
            "",
            tint = Color.Black,
            modifier = modifier.rotate(infiniteRotation()).size(72.dp)
        )
}