package com.example.quickdraw.duel.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.quickdraw.R
import com.example.quickdraw.duel.DuelGameLogic
import com.example.quickdraw.duel.DuelNavigation
import com.example.quickdraw.game.repo.GameRepository
import kotlinx.coroutines.delay

@Composable
fun PresentationScreen(controller: NavHostController, gameLogic: DuelGameLogic, repo: GameRepository){
    if(true){
        LaunchedEffect(true) {
            delay(3000)
            controller.navigate(DuelNavigation.WeaponSelect)
        }
    }
    DuelContainer(controller,gameLogic,repo,{
        Box(modifier=Modifier.padding(5.dp).fillMaxSize()){
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.stars_2_24px),
                "",
                tint = Color.Black,
                modifier = Modifier.align(alignment = Alignment.Center)
            )
        }
    })
}