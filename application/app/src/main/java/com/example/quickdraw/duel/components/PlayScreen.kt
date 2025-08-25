package com.example.quickdraw.duel.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.DurationBasedAnimationSpec
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.quickdraw.R
import com.example.quickdraw.duel.DuelGameLogic
import com.example.quickdraw.duel.DuelNavigation
import com.example.quickdraw.duel.MatchResult
import com.example.quickdraw.duel.PeerState
import com.example.quickdraw.game.components.infiniteRotation
import com.example.quickdraw.game.repo.GameRepository
import com.example.quickdraw.ui.theme.Typography
import com.google.android.material.progressindicator.LinearProgressIndicatorSpec
import kotlinx.coroutines.delay

@Composable
fun PlayScreen(controller: NavHostController, gameLogic: DuelGameLogic){
    val shouldShoot = gameLogic.shouldShoot.collectAsState().value
    val roundEnded = (gameLogic.selfState.collectAsState().value == PeerState.BANG && gameLogic.otherState.collectAsState().value == PeerState.BANG)
    val lostRound = gameLogic.didSelfWin() == MatchResult.LOST
    val duration = 2000 //milliseconds
    val bloodAnimation = animateFloatAsState(
        targetValue = if(roundEnded && lostRound) 1.0f else 0.0f,
        animationSpec = TweenSpec(duration, 0, LinearEasing)
    )
    LaunchedEffect(roundEnded) {
        if(roundEnded){
            delay(duration.toLong())
            controller.navigate(DuelNavigation.Results)
        }
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize().clickable(onClick = gameLogic::bang).background(
            if(shouldShoot) Color.Green else Color.Yellow
        )
    ){
        if(roundEnded && lostRound){
            Box(modifier = Modifier.animateContentSize().fillMaxSize().scale(1.0f, bloodAnimation.value).background(Color.Red) )
        }
        if(shouldShoot){
            Text("SHOOT!", fontSize = Typography.titleLarge.fontSize)
        } else {
            Text("Steady")
        }
    }
}