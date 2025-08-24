package com.example.quickdraw.duel.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.quickdraw.R
import com.example.quickdraw.duel.DuelGameLogic
import com.example.quickdraw.game.components.infiniteRotation
import com.example.quickdraw.game.repo.GameRepository
import com.example.quickdraw.ui.theme.Typography

@Composable
fun PlayScreen(gameLogic: DuelGameLogic){
    val shouldShoot = gameLogic.shouldShoot.collectAsState().value
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize().clickable(onClick = gameLogic::bang).background(
            if(shouldShoot) Color.Green else Color.Yellow
        )
    ){
        if(shouldShoot){
            Text("SHOOT!", fontSize = Typography.titleLarge.fontSize)
        } else {
            Text("Steady")
        }
    }
}