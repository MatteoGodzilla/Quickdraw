package com.example.quickdraw.duel.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
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

@Composable
fun PlayScreen(gameLogic: DuelGameLogic){
    /*
    DuelContainer(controller,gameLogic,repo) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.local_police_24px),
                "",
                tint = Color.Black,
                modifier = Modifier.size(72.dp)
            )
        }
    }

     */
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize().clickable(onClick = gameLogic::bang)
    ){
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.local_police_24px),
            "",
            tint = Color.Black,
            modifier = Modifier.size(72.dp)
        )
    }
}